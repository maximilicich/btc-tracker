package ar.com.wnc.btctracker.domain;

import ar.com.wnc.btctracker.util.MathUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class BitcoinPriceStats {

    private Date timestampFrom;
    private Date timestampTo;
    private Double maxPrice;
    private Double avgPrice;

    public Double getPercentPriceDiff() {
        return MathUtils.calculatePercentDiff(this.maxPrice, this.avgPrice);
    }
}
