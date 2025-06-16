package edu.istu.achipiga;

import java.io.*;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
public class Organization {
    /**
     * 
     */
    private String id;

    /**
     * 
     */
    public String name;

    /**
     * 
     */
    public List<CheckoutRegister> checkoutRegisters;

    /**
     * 
     */
    public List<Employee> workers;

    /**
     * 
     */
    public Location location;

    /**
     * 
     */
    public String inn;

    /**
     * 
     */
    public Employee boss;

    public Organization(String id, String name, List<Employee> workers, Location location, String inn, Employee boss) {
        this.id = id;
        this.name = name;
        this.workers = workers;
        this.location = location;
        this.inn = inn;
        this.boss = boss;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", checkoutRegisters=" + checkoutRegisters +
                ", workers=" + workers +
                ", location=" + location +
                ", inn='" + inn + '\'' +
                ", boss=" + boss +
                '}';
    }
}