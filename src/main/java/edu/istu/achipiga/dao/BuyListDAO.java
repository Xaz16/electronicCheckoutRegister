package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BuyListDAO extends BaseDAO<BuyList> {

    @Override
    public BuyList getCurrentInstance() {
        return super.getCurrentInstance();
    }

    @Override
    public void setCurrentInstance(BuyList instance) {
        super.setCurrentInstance(instance);
    }

    @Override
    public List<BuyList> getAll() {
        return new ArrayList<>();
    }

    @Override
    public BuyList save(BuyList buyList) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO buy_lists (totalSum) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, buyList.getTotalSum().longValue());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int buyListId = rs.getInt(1);
                buyList.setId(buyListId);

                String itemsSql = "INSERT INTO buy_list_items (quantity, bought_price, buy_lists, product_id) VALUES (?, ?, ?, ?)";
                PreparedStatement itemsStmt = conn.prepareStatement(itemsSql);
                
                for (BuyListItem item : buyList.getItems()) {
                    itemsStmt.setInt(1, item.getQuantity());
                    itemsStmt.setLong(2, item.getBoughtPrice().longValue());
                    itemsStmt.setInt(3, buyListId);
                    itemsStmt.setInt(4, Integer.parseInt(item.getProduct().getId()));
                    itemsStmt.executeUpdate();
                }

                buyList.setId(buyListId);
                return buyList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BuyList getById(int id) {
        try (Connection conn = getConnection()) {
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
                Product product = new ProductDAO().getById(productId);    
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

    @Override
    void delete(BuyList instance) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    BuyList getCurrent() {
        throw new UnsupportedOperationException("Unimplemented method 'getCurrent'");
    }
} 