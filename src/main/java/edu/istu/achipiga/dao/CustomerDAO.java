package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {
    private static Customer curr = null;

    public static Customer getCurrentCustomer() {
        if(curr != null) return curr;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            int currentCustomerId = 1;
            ResultSet customerRs = conn.createStatement().executeQuery("SELECT * FROM customers WHERE id = " + currentCustomerId);
            ResultSet bankCardRs = conn.createStatement().executeQuery("SELECT * FROM bank_cards WHERE customer_id = " + currentCustomerId);
            ResultSet discountCardRs = conn.createStatement().executeQuery("SELECT * FROM discount_cards WHERE customer_id = " + currentCustomerId);

            curr = new Customer(
                    customerRs.getString("id"),
                    customerRs.getString("name"),
                    new DiscountCard(DiscountTypes.valueOf(discountCardRs.getString("type")), discountCardRs.getString("id")),
                    new BankCard(bankCardRs.getString("id"), bankCardRs.getString("number"), bankCardRs.getString("cvv"), bankCardRs.getString("expire_date"))

            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return curr;
    }
}
