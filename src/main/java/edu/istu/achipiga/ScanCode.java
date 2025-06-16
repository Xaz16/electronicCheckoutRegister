package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Setter;

import java.io.*;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
public class ScanCode {
    /**
     * 
     */
    @Generated
    private String id;

    /**
     * 
     */
    public String number;

    /**
     * 
     */
    @Setter
    public Product product;

    public ScanCode(String number) {
        this.number = number;
    }

}