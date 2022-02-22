package ar.com.wnc.btctracker.scheduler;

import ar.com.wnc.btctracker.domain.BitcoinPrice;
import ar.com.wnc.btctracker.domain.CriptoPriceResponse;
import ar.com.wnc.btctracker.service.BitcoinPriceProxyService;
import ar.com.wnc.btctracker.service.BitcoinService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class BitcoinTracker {

    @Autowired
    BitcoinService bitcoinService;

    @Autowired
    BitcoinPriceProxyService bitcoinPriceProxyService;

    @Scheduled(fixedDelayString = "${bitcointracker.fixedDelay}")
    private void trackBtc() {

        bitcoinPriceProxyService.getBitcoinPrice().subscribe(jsonResponse -> {
            Gson gson = new GsonBuilder().create();
            CriptoPriceResponse criptoPriceResponse = gson.fromJson(jsonResponse, CriptoPriceResponse.class);
            log.info(String.valueOf(criptoPriceResponse));
            bitcoinService.createBitcoinPrice(new BitcoinPrice(
                    criptoPriceResponse.getCurr2(),
                    Double.valueOf(criptoPriceResponse.getLprice())));
        });

    }

}
