package edu.istu.achipiga;

import lombok.Getter;
import lombok.Setter;



/**
 * @author Aleksey Chipiga
 */
public class CheckoutRegister {
    /**
     * 
     */
    @Getter
    private int id;

    /**
     * 
     */
    @Getter
    @Setter
    private BuyList buyList = new BuyList();

    /**
     * 
     */
    @Getter
    private Workshift workshift;

    /**
     * 
     */
    @Getter
    private Organization organization;


    public CheckoutRegister(int id, BuyList buyList, Workshift workshift, Organization organization) {
        this.id = id;
        this.buyList = buyList;
        this.workshift = workshift;
        this.organization = organization;
    }

}