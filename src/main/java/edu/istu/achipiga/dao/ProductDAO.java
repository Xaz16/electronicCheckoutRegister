package edu.istu.achipiga.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDAO {
    private static ObservableList<edu.istu.achipiga.Product> curr = null;

    public static ObservableList<edu.istu.achipiga.Product> getAllProducts() {
        if(curr != null) return curr;
        ObservableList<edu.istu.achipiga.Product> products = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM products");
            while (rs.next()) {
                products.add(new edu.istu.achipiga.Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        BigDecimal.valueOf(rs.getLong("price"))
                ));
            }
            curr = products;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return curr;
    }

    public static edu.istu.achipiga.Product getById(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String sql = "SELECT * FROM products WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                return null;
            }
            
            return new edu.istu.achipiga.Product(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("category"),
                BigDecimal.valueOf(rs.getLong("price"))
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}