package ar.com.wnc.btctracker.dao.jpa;

import ar.com.wnc.btctracker.domain.BitcoinPrice;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository can be used to delegate CRUD operations against the data source: http://goo.gl/P1J8QH
 */
public interface BitcoinRepository extends CrudRepository<BitcoinPrice, Long> {

}
