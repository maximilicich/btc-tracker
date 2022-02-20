package ar.com.wnc.btctracker.api.rest;

import ar.com.wnc.btctracker.domain.BitcoinPrice;
import ar.com.wnc.btctracker.exception.DataFormatException;
import ar.com.wnc.btctracker.service.BitcoinService;
import ar.com.wnc.btctracker.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/*
 * Demonstrates how to set up RESTful API endpoints using Spring MVC
 */

@RestController
@RequestMapping(value = "/api/v1/bitcoin")
@Api(tags = {"bitcoins"})
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
    @ApiOperation(value = "Get a list of all bitcoins.", notes = "Notas adicionales")
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


    @RequestMapping(value = "",
            method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a list of all bitcoins.", notes = "Notas adicionales")
    public
    @ResponseBody
    ResponseEntity getAllBitcoins(@RequestParam String from, @RequestParam String to,
                                  HttpServletRequest request, HttpServletResponse response) {

        Date dFrom = stringToDate(from);
        Date dTo = stringToDate(to);

        Iterable<BitcoinPrice> iterable = this.bitcoinService.getAllBitcoinPrices();

        Double result =
                StreamSupport.stream(iterable.spliterator(), false)
                        .collect(Collectors.toList()).stream()
                        .filter(p -> p.getTs().after(dFrom) && p.getTs().before(dTo))
                        .mapToDouble(BitcoinPrice::getPrice)
                        .average()
                        .orElse(Double.NaN);

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);

    }

    private ResponseEntity getBitcoinPricesAll() {

        List<BitcoinPrice> allBitcoinPrices = this.bitcoinService.getAllBitcoinPrices();

        return ResponseEntity.status(HttpStatus.OK)
                .body(allBitcoinPrices);

    }

    private ResponseEntity getBitcoinPriceAt(String ts) {

        Date dts = this.stringToDate(ts);
        log.debug(String.format("getBitcoinPriceAt(string: %s -> date: %s)", ts, dts));

        BitcoinPrice result = this.bitcoinService.getBitcoinPriceAt(dts)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("No hay precio de bitcoin registrado para el timestamp: %s", ts)));

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);

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

        log.debug(String.format("stringToDate: timestampFormat is '%s'", timestampFormat));

        DateFormat df = new SimpleDateFormat(timestampFormat);

        try {
            return df.parse(ts);
        } catch (ParseException e) {
            throw new DataFormatException(
                    String.format("No se pudo convertir a timestamp '%s' (formato esperado: '%s')", ts, timestampFormat),
                    e);
        }
    }

}
