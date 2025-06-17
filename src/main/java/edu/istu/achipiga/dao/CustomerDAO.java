package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerDAO extends BaseDAO<Customer> {
    

    @Override
    public Customer getCurrent() {
        if(super.getCurrentInstance() != null) return super.getCurrentInstance();
        try (Connection conn = getConnection()) {
            int currentCustomerId = 1;
            ResultSet customerRs = conn.createStatement().executeQuery("SELECT * FROM customers WHERE id = " + currentCustomerId);
            ResultSet bankCardRs = conn.createStatement().executeQuery("SELECT * FROM bank_cards WHERE customer_id = " + currentCustomerId);
            ResultSet discountCardRs = conn.createStatement().executeQuery("SELECT * FROM discount_cards WHERE customer_id = " + currentCustomerId);

            super.setCurrentInstance(new Customer(
                    customerRs.getString("id"),
                    customerRs.getString("name"),
                    new DiscountCard(DiscountTypes.valueOf(discountCardRs.getString("type")), discountCardRs.getString("id")),
                    new BankCard(bankCardRs.getString("number"), bankCardRs.getString("cvv"), bankCardRs.getString("expire_date"), bankCardRs.getString("id"))

            ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return super.getCurrentInstance();
    }

    @Override
    Customer getById(int id) {
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    List<Customer> getAll() {
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    Customer save(Customer instance) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    void delete(Customer instance) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
