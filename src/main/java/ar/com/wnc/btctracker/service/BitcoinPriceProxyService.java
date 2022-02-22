package ar.com.wnc.btctracker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BitcoinPriceProxyService {

    @Value("${bitcointracker.url}")
    private String url;

    public Mono<String> getBitcoinPrice() {

        log.info(String.format("Tracking BTC from URL: %s ...", this.url));

        return WebClient.create()
                .get()
                .uri(this.url)
                .retrieve().bodyToMono(String.class).single();
    }

}
