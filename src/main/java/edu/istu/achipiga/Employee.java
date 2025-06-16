package edu.istu.achipiga;

import javafx.collections.ObservableArray;
import lombok.Getter;

import java.io.*;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
public class Employee {
    /**
     * 
     */
    @Getter
    private int id;

    /**
     * 
     */
    @Getter
    private String name;

    private String position;

    public Employee(int id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }


    @Override
    public String toString() {
        return name + " (" + position + ")";
    }
}