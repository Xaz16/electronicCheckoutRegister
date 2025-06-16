package edu.istu.achipiga;

import java.io.*;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
public abstract class Device {

    /**
     * Default constructor
     */
    public Device() {
    }

    /**
     * 
     */
    private String id;

    /**
     * 
     */
    public String name;


    /**
     * @return
     */
    protected void sendEvent() {
        // TODO implement here
    }

}