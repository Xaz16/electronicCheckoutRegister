package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    private int id;

    @Setter
    @Getter
    public String time;

    @Getter
    private Customer customer;

    @Getter
    private CheckoutRegister checkoutRegister;

    @Getter
    private BigDecimal providedSum;

    @Getter
    @Setter
    private BigDecimal discountAmount;

    @Getter
    @Setter
    private BigDecimal totalAmount;

    @Getter
    private PaymentMethods paymentMethod;

    @Getter
    private ReceiptTypes receiptType;

    @Getter
    @Setter
    private BuyList buyList;

    @Getter
    @Setter
    private BankCard bankCard;


    public Receipt(Customer customer, CheckoutRegister checkoutRegister, BigDecimal providedSum, PaymentMethods paymentMethod) {
        this.id = new Random().nextInt(1_000_001);
        this.time = java.time.Instant.now().toString();
        this.customer = customer;
        this.checkoutRegister = checkoutRegister;
        this.providedSum = providedSum;
        this.paymentMethod = paymentMethod;
        this.totalAmount = checkoutRegister.getBuyList().getTotalSum();
        this.discountAmount = BigDecimal.ZERO; 
        this.receiptType = ReceiptTypes.PAYMENT;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getFinalSum() {
        return totalAmount.subtract(discountAmount);
    }

    public BigDecimal getExchange() {
        return providedSum.subtract(getFinalSum());
    }

}