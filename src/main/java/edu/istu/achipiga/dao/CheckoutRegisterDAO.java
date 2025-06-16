package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckoutRegisterDAO {
    private static CheckoutRegister curr = null;

    public static CheckoutRegister getCurrentCheckoutRegister() {
        if(curr != null) return curr;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            Organization org = OrganizationDAO.getCurrentOrganization();
            ResultSet checkoutRs = conn.createStatement().executeQuery("SELECT * FROM checkout_registers WHERE organization_id = 1");
            ResultSet workshiftRs = conn.createStatement().executeQuery("SELECT * FROM workshifts WHERE employee_id = 6");
            ResultSet employeeRs = conn.createStatement().executeQuery("SELECT * FROM employees WHERE id = 6");

            curr = new CheckoutRegister(
                    checkoutRs.getInt("id"),
                    new ArrayList<>(),
                    new BuyList(),
                    new Workshift(
                            workshiftRs.getInt("id"),
                            new Employee(
                                    employeeRs.getInt("id"),
                                    employeeRs.getString("name"),
                                    employeeRs.getString("position")
                            ),
                            workshiftRs.getString("start_time"),
                            workshiftRs.getString("name")
                    ),
                    org
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return curr;
    }
}
