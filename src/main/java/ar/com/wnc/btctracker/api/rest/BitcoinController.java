package ar.com.wnc.btctracker.api.rest;

import ar.com.wnc.btctracker.domain.BitcoinPrice;
import ar.com.wnc.btctracker.exception.DataFormatException;
import ar.com.wnc.btctracker.exception.ResourceNotFoundException;
import ar.com.wnc.btctracker.service.BitcoinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * Demonstrates how to set up RESTful API endpoints using Spring MVC
 */

@RestController
@RequestMapping(value = "/api/v1/bitcoin")
@Api(tags = {"bitcoin"})
@Slf4j
public class BitcoinController extends AbstractRestHandler {

    @Autowired
    private BitcoinService bitcoinService;

    @Value("${bitcointracker.timestampFormat}")
    private String timestampFormat;


    @RequestMapping(value = "/prices",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Consulta precios BTC (lista completa o un timestamp dado)",
            notes = "Por default consulta la lista completa de precios registrados. Con el query parm opcional ts" +
                    " se puede consultar el precio para un timestamp determinado. " +
                    "En caso de no haber precio registrado para ese timestamp exacto, el precio brindado será el " +
                    " inmediato anterior registrado.")
    public
    @ResponseBody
    ResponseEntity getBitcoinPrices(@RequestParam(required = false) String ts) {

        /*
        TODO pensar una mejor alternativa REST para esto
        Si se recibe ts, devuelve un Price, pero si no se recibe devuelve la lista completa
        tal vez se pueda pensar en dos endpoints diferentes para evitar la ambiguedad del tipo de respuesta
        a partir del mismo uri path
         */
        return (ts == null) ? getBitcoinPricesAll() : getBitcoinPriceAt(ts);
    }


    @RequestMapping(value = "/stats",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Consulta estadisticas de precios BTC para un periodo dado",
            notes = "El periodo a consultar esta dado por los query parms obligatorios ts_from y ts_to. " +
                    "La información estadistica brindada es el precio promedio entre ambos Timestamps " +
                    "así como la diferencia porcentual entre " +
                    "ese valor promedio y el valor máximo almacenado para toda la serie temporal disponible.")
    public
    @ResponseBody
    ResponseEntity getBitcoinStats(@RequestParam(name="ts_from") String from,
                                   @RequestParam(name="ts_to") String to,
                                   HttpServletRequest request, HttpServletResponse response) {

        Date dFrom = stringToDate(from);
        Date dTo = stringToDate(to);

        validateTimestampRange(dFrom, dTo);

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.bitcoinService.getBitcoinPriceStats(dFrom, dTo));

    }

    /**
     *
     * @return
     */
    private ResponseEntity getBitcoinPricesAll() {

        List<BitcoinPrice> allBitcoinPrices = this.bitcoinService.getAllBitcoinPrices();

        return ResponseEntity.status(HttpStatus.OK)
                .body(allBitcoinPrices);

    }


    private ResponseEntity getBitcoinPriceAt(String ts) {

        Date dts = this.stringToDate(ts);
        log.debug(String.format("getBitcoinPriceAt(string: %s -> date: %s)", ts, dts));

        avoidFutureDates(dts);

        BitcoinPrice result = this.bitcoinService.getBitcoinPriceAt(dts)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No hay precio de bitcoin registrado para el timestamp: %s", ts)));

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);

    }

    private DateFormat buildDateFormat() {
        return new SimpleDateFormat(this.timestampFormat);
    }

    /**
     * Por el momento, para simplificar, tomamos los requestParms de timestamp como Strings
     * y los convertimos de este modo a Date
     * En realidad, sería mejor luego que el datatype de los requestParms sea directamente java.util.Date
     * con el @DateTimeFormat(pattern="yyyy-MM-dd-HH...")
     * TODO evaluar posibilidad de tener @RequestParams de tipo Date
     *
     * @param ts
     * @return
     */
    private Date stringToDate(String ts) {

        log.debug(String.format("stringToDate(%s): timestampFormat is '%s'", ts, this.timestampFormat));

        try {
            return buildDateFormat().parse(ts);
        } catch (ParseException e) {
            throw new DataFormatException(
                    String.format("No se pudo convertir a timestamp '%s' (formato esperado: '%s')", ts, timestampFormat),
                    e);
        }
    }

    /**
     * Verifica que la fecha date no sea futura, caso contrario lanza DataFormatException (400 BAD REQUEST)
     * @param date
     */
    private void avoidFutureDates(Date date) {

        Date now = new Date();
        if (date.after(now)) {
            throw new DataFormatException(
                    String.format("El timestamp ingresado (%s) no puede ser posterior a la fecha/hora actual.",
                            buildDateFormat().format(date)));
        }

    }

    /**
     * Valida que el periodo definido por dos dates sea valido (d1 <= d2)
     * Caso contrario lanza DataFormatException (400 BAD REQUEST)
     *
     * @param d1
     * @param d2
     */
    private void validateTimestampRange(Date d1, Date d2) {
        if (d1.after(d2)) {
            throw new DataFormatException(
                    String.format("El periodo definido por los timestamps ingresados (%s-%s) es inválido ",
                            buildDateFormat().format(d1),
                            buildDateFormat().format(d2)));
        }
    }

}
