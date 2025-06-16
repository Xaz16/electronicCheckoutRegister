package edu.istu.achipiga;

import lombok.AllArgsConstructor;

import java.io.*;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
@AllArgsConstructor
public class Location {

    /**
     * 
     */
    public String country;

    /**
     * 
     */
    public String city;

    /**
     * 
     */
    public String street;

    /**
     * 
     */
    public String build;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(country).append("\n");
        sb.append(city).append("\n");
        sb.append(street).append(", ");
        sb.append(build);

        return sb.toString();
    }
}