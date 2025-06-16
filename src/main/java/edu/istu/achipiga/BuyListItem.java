package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */

public class BuyListItem {

    /**
     * 
     */
    @Getter
    private Product product;

    /**
     * 
     */
    @Getter
    @Setter
    private int quantity = 1;

    /**
     * 
     */
    @Getter
    private int id = new Random().nextInt(1_000_001);

    @Getter
    @Setter
    private BigDecimal boughtPrice;

    public BuyListItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.boughtPrice = product.getPrice();
    }

    public BigDecimal getTotalSum() {
        return boughtPrice.multiply(BigDecimal.valueOf(quantity));
    }
}