package edu.istu.achipiga;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 
 */
@Getter
public enum DiscountTypes {
    bronze(new BigDecimal("0.05")),
    silver(new BigDecimal("0.10")),
    gold(new BigDecimal("0.15")),
    platinum(new BigDecimal("0.20"));

    private final BigDecimal discount;

    DiscountTypes(BigDecimal discount) {
        this.discount = discount;
    }

}