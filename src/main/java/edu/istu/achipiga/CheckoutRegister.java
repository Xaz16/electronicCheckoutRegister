package edu.istu.achipiga;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;



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
    private Device device;

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


    public CheckoutRegister(int id, Device device, BuyList buyList, Workshift workshift, Organization organization) {
        this.id = id;
        this.device = device;
        this.buyList = buyList;
        this.workshift = workshift;
        this.organization = organization;
    }

    /**
     * @return
     */
    public void finalizeBuyList() {
        buyList.getTotalSum();

        buyList = new BuyList();
    }

    /**
     * @param type 
     * @param listener 
     * @return
     */
    public void listenDeviceEvent(DeviceEventTypes type, Listener listener) {
        // TODO implement here

    }

}