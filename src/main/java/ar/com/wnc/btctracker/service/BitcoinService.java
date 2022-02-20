package ar.com.wnc.btctracker.service;

import ar.com.wnc.btctracker.dao.jpa.BitcoinRepository;
import ar.com.wnc.btctracker.domain.BitcoinPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/*
 * Sample service to demonstrate what the API would use to get things done
 */
@Slf4j
@Service
public class BitcoinService {

    @Autowired
    private BitcoinRepository bitcoinRepository;

    public BitcoinService() {
    }

    public BitcoinPrice createBitcoinPrice(BitcoinPrice bitcoinPrice) {
        return bitcoinRepository.save(bitcoinPrice);
    }

    public List<BitcoinPrice> getAllBitcoinPrices() {
        return bitcoinRepository.findAll();
    }

    public Optional<BitcoinPrice> getBitcoinPriceAt(Date ts) {

        List<BitcoinPrice> allPrices = bitcoinRepository.findAll();

        return allPrices.stream()
                        .filter(p -> ! p.getTs().after(ts))
                        .reduce((a, b) -> b);   // reduce((a,b)->b) devuelve el ultimo elemento

    }

}
