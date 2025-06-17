package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;

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