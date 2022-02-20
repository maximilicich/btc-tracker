package ar.com.wnc.btctracker.service;

import ar.com.wnc.btctracker.dao.jpa.BitcoinRepository;
import ar.com.wnc.btctracker.domain.BitcoinPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public BitcoinPrice createBitcoin(BitcoinPrice bitcoinPrice) {
        return bitcoinRepository.save(bitcoinPrice);
    }

    public Iterable<BitcoinPrice> getAllBitcoins() {
        return bitcoinRepository.findAll();

    }

}
