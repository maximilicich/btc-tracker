package ar.com.wnc.btctracker;

import ar.com.wnc.btctracker.domain.BitcoinPrice;
import ar.com.wnc.btctracker.domain.BitcoinPriceStats;
import ar.com.wnc.btctracker.service.BitcoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class BtcTrackerApplicationTests {

//	@Autowired
//	private BitcoinController bitcoinController;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BitcoinService bitcoinService;

	@BeforeEach
	void loadDummyPrices() throws ParseException {
		if (bitcoinService.getAllBitcoinPrices().size() == 0) {
			bitcoinService.createBitcoinPrice(new BitcoinPrice("USD", 10.0,
					DATE_FORMAT.parse("2022-02-21 20:00:10")));
			bitcoinService.createBitcoinPrice(new BitcoinPrice("USD", 14.0,
					DATE_FORMAT.parse("2022-02-21 20:00:20")));
			bitcoinService.createBitcoinPrice(new BitcoinPrice("USD", 12.0,
					DATE_FORMAT.parse("2022-02-21 20:00:30")));
			bitcoinService.createBitcoinPrice(new BitcoinPrice("USD", 11.0,
					DATE_FORMAT.parse("2022-02-21 20:00:40")));
		}
	}

	@Test
	void contextLoads() {
		assertThat(mockMvc).isNotNull();
		assertThat(bitcoinService).isNotNull();
	}

	@Test
	void testGetAllPrices() {

		assertEquals(bitcoinService.getAllBitcoinPrices().size(), 4);
	}

	@Test
	void testMaxAndAvgPrice() throws ParseException {

		BitcoinPriceStats stats = bitcoinService.getBitcoinPriceStats(
				DATE_FORMAT.parse("2022-02-21 20:00:00"),
				DATE_FORMAT.parse("2022-02-21 20:00:50"));

		assertEquals(stats.getMaxPrice(), Double.valueOf(14.0));

		assertEquals(stats.getAvgPrice(), Double.valueOf(11.75));

	}

	@Test
	void testGetPriceAt() throws ParseException {

		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:10")),
				Double.valueOf(10.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:11")),
				Double.valueOf(10.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:12")),
				Double.valueOf(10.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:13")),
				Double.valueOf(10.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:14")),
				Double.valueOf(10.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:18")),
				Double.valueOf(10.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:19")),
				Double.valueOf(10.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:20")),
				Double.valueOf(14.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:21")),
				Double.valueOf(14.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:29")),
				Double.valueOf(14.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:30")),
				Double.valueOf(12.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:39")),
				Double.valueOf(12.0));
		assertEquals(getBtcPriceValueAt(DATE_FORMAT.parse("2022-02-21 20:00:40")),
				Double.valueOf(11.0));
	}


	private Double getBtcPriceValueAt(Date ts) {
		Optional<BitcoinPrice> bp = bitcoinService.getBitcoinPriceAt(ts);
		return bp.isPresent() ? bp.get().getPrice() : Double.NaN;
	}


}
