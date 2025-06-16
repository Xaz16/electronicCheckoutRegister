package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
@AllArgsConstructor
public class Receipt {
    /**
     * 
     */
    @Getter
    private final int id = new Random().nextInt(1_000_001);

    public final String time = java.time.Instant.now().toString();

    @Getter
    private Customer customer;

    @Getter
    private CheckoutRegister checkoutRegister;

    @Getter
    private BigDecimal providedSum;

    @Getter
    PaymentMethods paymentMethod;

    public BigDecimal getAmount() {
        return checkoutRegister.getBuyList().getTotalSum();
    }

    public BigDecimal getChange() {
        return paymentMethod.equals(PaymentMethods.CASH) ?
                providedSum.subtract(getAmount()) : BigDecimal.ZERO;
    }

    public String getPaymentMethodLabelString() {
        return switch (paymentMethod) {
            case CASH -> "Наличными";
            case CARD -> "Банковской картой";
        };
    }
}