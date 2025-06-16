package edu.istu.achipiga.dao;

import edu.istu.achipiga.CheckoutRegister;
import edu.istu.achipiga.Receipt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigInteger;
import java.sql.*;


public class ReceiptDAO {

    public static void insertNew(Receipt receipt) {
        String insertSQL = "INSERT INTO receipts(time, workshift_id, customer_id, bank_card_id, checkout_register_id, buy_list_id) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            pstmt.setString(1, receipt.time);
            pstmt.setInt(2, receipt.getCheckoutRegister().getWorkshift().getId());
            pstmt.setInt(3, Integer.parseInt(receipt.getCustomer().getId()));
            pstmt.setInt(4, Integer.parseInt(receipt.getCustomer().getBankCard().getId()));
            pstmt.setInt(5, receipt.getCheckoutRegister().getId());
            pstmt.setInt(6, receipt.getCheckoutRegister().getBuyList().getId());


            System.out.println("Чек добавлен в базу данных");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
