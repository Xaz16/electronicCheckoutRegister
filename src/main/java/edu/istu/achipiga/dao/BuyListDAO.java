package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;
import java.math.BigDecimal;

public class BuyListDAO {
    public static BuyList getById(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String sql = "SELECT * FROM buy_lists WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                return null;
            }
            
            BuyList buyList = new BuyList();
            buyList.setId(id);
            
            String itemsSql = "SELECT * FROM buy_list_items WHERE buy_lists = ?";
            PreparedStatement itemsStmt = conn.prepareStatement(itemsSql);
            itemsStmt.setInt(1, id);
            ResultSet itemsRs = itemsStmt.executeQuery();
            
            while (itemsRs.next()) {
                int productId = itemsRs.getInt("product_id");
                Product product = ProductDAO.getById(productId);
                if (product != null) {
                    BuyListItem item = new BuyListItem(product, itemsRs.getInt("quantity"));
                    item.setBoughtPrice(BigDecimal.valueOf(itemsRs.getLong("bought_price")));
                    buyList.addItem(item);
                }
            }
            
            return buyList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
} 