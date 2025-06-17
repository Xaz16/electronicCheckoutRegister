package edu.istu.achipiga.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.istu.achipiga.Product;

public class ProductDAO extends BaseDAO<edu.istu.achipiga.Product> {

    @Override
    public List<edu.istu.achipiga.Product> getAll() {

        List<edu.istu.achipiga.Product> products = new ArrayList<>();
        try (Connection conn = getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM products" + (context.getSearchQuery() != null ? " WHERE name LIKE '%" + context.getSearchQuery() + "%'" : ""));
            while (rs.next()) {
                products.add(new edu.istu.achipiga.Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        BigDecimal.valueOf(rs.getLong("price"))
                ));
            }
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public edu.istu.achipiga.Product getById(int id) {
        try (Connection conn = getConnection()) {
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

    @Override
    void delete(edu.istu.achipiga.Product instance) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    Product save(Product instance) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    Product getCurrent() {
        throw new UnsupportedOperationException("Unimplemented method 'getCurrent'");
    }
    
}