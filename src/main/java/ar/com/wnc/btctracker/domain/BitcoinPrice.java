package ar.com.wnc.btctracker.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/*
 * a simple domain entity doubling as a DTO
 */
@Getter
@Setter
@Entity
@Table(name = "bitcoin_price")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitcoinPrice {

    @Id
    @GeneratedValue()
    private long id;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Double price;

    @Column()
    @CreationTimestamp
    private Date ts;

    public BitcoinPrice() {
    }

    public BitcoinPrice(String currency, Double price) {
        this.currency = currency;
        this.price = price;
        this.ts = new Date();
    }

    public BitcoinPrice(String currency, Double price, Date date) {
        this.currency = currency;
        this.price = price;
        this.ts = date;
    }

}
