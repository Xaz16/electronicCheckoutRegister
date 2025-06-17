package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            if(receipt.getBankCard() != null) {
                pstmt.setInt(4, Integer.parseInt(receipt.getBankCard().getId()));
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
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

    public static List<Receipt> loadReceipts() {
        List<Receipt> receipts = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String sql = "SELECT * FROM receipts ORDER BY time DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            while (rs.next()) {
                Receipt receipt = ReceiptDAO.getById(rs.getInt("id"));
                if (receipt != null) {
                    receipts.add(receipt);
                }
            }
            
            return receipts;
        } catch (SQLException e) {
            System.out.println("Error loading receipts: " + e.getMessage());
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
            
            CheckoutRegister register = CheckoutRegisterDAO.getById(receiptRs.getInt("checkout_register_id"));
            if (register == null) {
                return null;
            }

            Customer currentCustomer = CustomerDAO.getCurrentCustomer();
            
            Receipt receipt = new Receipt(
                currentCustomer,
                register,
                receiptRs.getBigDecimal("provided_sum"),
                PaymentMethods.valueOf(receiptRs.getString("payment_method"))
            );
            receipt.setId(id);
            receipt.time = receiptRs.getString("time");
            receipt.setDiscountAmount(receiptRs.getBigDecimal("discount_amount"));
            receipt.setBuyList(BuyListDAO.getById(receiptRs.getInt("buy_list_id")));
            receipt.setTotalAmount(receiptRs.getBigDecimal("total_amount"));

            if(receiptRs.getInt("bank_card_id") != 0) {
                receipt.setBankCard(currentCustomer.getBankCard());
            }

            return receipt;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
