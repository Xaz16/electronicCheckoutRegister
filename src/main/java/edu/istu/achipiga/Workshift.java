package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
@AllArgsConstructor
public class Workshift {
    /**
     * 
     */
    @Getter
    private int id;

    /**
     * 
     */
    @Getter
    private Employee employee;

    /**
     * 
     */
    @Getter
    private String startTime;

    /**
     * 
     */
    @Getter
    private String name;
}