package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author Aleksey Chipiga
 */
@AllArgsConstructor
public class Customer {
        /**
     * 
     */
    @Getter
    private String id;

    /**
     * 
     */
    @Getter
    private String name;

    /**
     * 
     */
    @Getter
    private DiscountCard discountCard;

    /**
     *
     */
    @Getter
    private BankCard bankCard;
}