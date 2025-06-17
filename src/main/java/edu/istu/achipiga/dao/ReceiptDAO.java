package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;

public class ReceiptDAO {
    public static Receipt insertNew(Receipt receipt) {
        String insertSQL = "INSERT INTO receipts(time, workshift_id, customer_id, bank_card_id, checkout_register_id, buy_list_id, payment_method, receipt_type, provided_sum, discount_amount, total_amount) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);

            String workshiftSQL = "SELECT id FROM workshifts WHERE employee_id = 6 ORDER BY id DESC LIMIT 1";
            ResultSet workshiftRs = conn.createStatement().executeQuery(workshiftSQL);
            if (!workshiftRs.next()) {
                return null;
            }
            int workshiftId = workshiftRs.getInt("id");

            pstmt.setString(1, receipt.time);
            pstmt.setInt(2, workshiftId);
            pstmt.setInt(3, Integer.parseInt(receipt.getCustomer().getId()));
            pstmt.setInt(4, Integer.parseInt(receipt.getCustomer().getBankCard().getId()));
            pstmt.setInt(5, receipt.getCheckoutRegister().getId());
            pstmt.setInt(6, receipt.getCheckoutRegister().getBuyList().getId());
            pstmt.setString(7, receipt.getPaymentMethod().name());
            pstmt.setString(8, receipt.getReceiptType().name());
            pstmt.setBigDecimal(9, receipt.getProvidedSum());
            pstmt.setBigDecimal(10, receipt.getDiscountAmount());
            pstmt.setBigDecimal(11, receipt.getTotalAmount());

            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return getById(newId);
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Receipt getById(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String receiptSQL = "SELECT * FROM receipts WHERE id = ?";
            PreparedStatement receiptStmt = conn.prepareStatement(receiptSQL);
            receiptStmt.setInt(1, id);
            ResultSet receiptRs = receiptStmt.executeQuery();
            
            if (!receiptRs.next()) {
                return null;
            }
            
            String customerSQL = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement customerStmt = conn.prepareStatement(customerSQL);
            customerStmt.setInt(1, receiptRs.getInt("customer_id"));
            ResultSet customerRs = customerStmt.executeQuery();
            
            if (!customerRs.next()) {
                return null;
            }
            
            String bankCardSQL = "SELECT * FROM bank_cards WHERE id = ?";
            PreparedStatement bankCardStmt = conn.prepareStatement(bankCardSQL);
            bankCardStmt.setInt(1, receiptRs.getInt("bank_card_id"));
            ResultSet bankCardRs = bankCardStmt.executeQuery();
            
            if (!bankCardRs.next()) {
                return null;
            }
            
            DiscountCard discountCard = null;
            String discountCardSQL = "SELECT * FROM discount_cards WHERE customer_id = ?";
            PreparedStatement discountCardStmt = conn.prepareStatement(discountCardSQL);
            discountCardStmt.setInt(1, customerRs.getInt("id"));
            ResultSet discountCardRs = discountCardStmt.executeQuery();
            
            if (discountCardRs.next()) {
                discountCard = new DiscountCard(
                    DiscountTypes.valueOf(discountCardRs.getString("type")),
                    discountCardRs.getString("id")
                );
            }
            
            Customer customer = new Customer(
                customerRs.getString("id"),
                customerRs.getString("name"),
                discountCard,
                new BankCard(
                    bankCardRs.getString("id"),
                    bankCardRs.getString("number"),
                    bankCardRs.getString("cvv"),
                    bankCardRs.getString("expire_date")
                )
            );
            
            CheckoutRegister register = CheckoutRegisterDAO.getById(receiptRs.getInt("checkout_register_id"));
            if (register == null) {
                return null;
            }
            
            Receipt receipt = new Receipt(
                customer,
                register,
                receiptRs.getBigDecimal("provided_sum"),
                PaymentMethods.valueOf(receiptRs.getString("payment_method"))
            );
            receipt.setId(id);
            receipt.time = receiptRs.getString("time");
            receipt.setDiscountAmount(receiptRs.getBigDecimal("discount_amount"));
            
            return receipt;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
