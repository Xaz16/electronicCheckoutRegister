package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author Aleksey Chipiga
 */

public class BankCard extends Card {
    /**
     * 
     */
    @Getter
    private String cvv;

    /**
     * 
     */
    @Getter
    private String expireDate;

    public BankCard(String number, String cvv, String expireDate, String id) {
        super(id, number);
        this.cvv = cvv;
        this.expireDate = expireDate;
    }

    @Override
    public String getCardType() {
        return "Bank Card";
    }
}