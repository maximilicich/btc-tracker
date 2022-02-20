package ar.com.wnc.btctracker.scheduler;

import ar.com.wnc.btctracker.domain.CriptoPriceResponse;
import ar.com.wnc.btctracker.domain.BitcoinPrice;
import ar.com.wnc.btctracker.service.BitcoinService;
import ar.com.wnc.btctracker.service.ServiceProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableScheduling
public class BitcoinTracker {

    @Autowired
    private ServiceProperties configuration;

    @Autowired
    BitcoinService bitcoinService;

    @Value("${bitcointracker.url}")
    private String url;

    @Scheduled(fixedDelayString = "${bitcointracker.fixedDelay}")
    private void trackBtc() {
        log.info(String.format("Tracking BTC from URL: %s ...", this.url));
        final String jsonResponse = WebClient.create()
                .get()
                .uri(this.url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.error("Initializing bitcoin price updater.");
                    return Mono.error(new RuntimeException("4xx - An error has occurred"));
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.error("Initializing bitcoin price updater.");
                    return Mono.error(new RuntimeException("5xx - Internal server error"));
                })
                .bodyToMono(String.class).block();

        Gson gson = new Gson();
        CriptoPriceResponse criptoPriceResponse = gson.fromJson(jsonResponse, CriptoPriceResponse.class);
        log.info(String.valueOf(criptoPriceResponse));
        bitcoinService.createBitcoinPrice(new BitcoinPrice(
                criptoPriceResponse.getCurr2(),
                Double.valueOf(criptoPriceResponse.getLprice())));

    }
}
