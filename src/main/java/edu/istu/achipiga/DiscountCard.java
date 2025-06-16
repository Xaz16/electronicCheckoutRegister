package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Aleksey Chipiga
 */
@AllArgsConstructor
public class DiscountCard {
    public DiscountTypes type;
    @Getter
    private final String id;

    public BigDecimal getDiscount() {
        return type.getDiscount();
    }
}