package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
@AllArgsConstructor
public class BankCard {
    @Getter
    private String id;
    /**
     * 
     */
    @Getter
    public String number;

    /**
     * 
     */
    @Getter
    public String cvv;

    /**
     * 
     */
    @Getter
    public String expireDate;
}