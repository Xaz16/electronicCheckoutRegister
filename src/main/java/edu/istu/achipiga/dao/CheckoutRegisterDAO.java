package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckoutRegisterDAO {
    public static CheckoutRegister getCurrentCheckoutRegister() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String sql = "SELECT * FROM checkout_registers ORDER BY id DESC LIMIT 1";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            if (!rs.next()) {
                return null;
            }
            
            int id = rs.getInt("id");
            int locationId = rs.getInt("location_id");
            int organizationId = rs.getInt("organization_id");
            
            Organization organization = OrganizationDAO.getById(organizationId);
            if (organization == null) {
                return null;
            }
            
            return new CheckoutRegister(id, new BuyList(), WorkshiftDAO.getById(1), organization);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CheckoutRegister getById(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String sql = "SELECT * FROM checkout_registers WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                return null;
            }
            
            int organizationId = rs.getInt("organization_id");
            Organization organization = OrganizationDAO.getById(organizationId);
            
            if (organization == null) {
                return null;
            }
            
            return new CheckoutRegister(id, new BuyList(), WorkshiftDAO.getById(1), organization);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<CheckoutRegister> getAll() {
        List<CheckoutRegister> registers = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String sql = "SELECT * FROM checkout_registers";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int organizationId = rs.getInt("organization_id");
                
                Organization organization = OrganizationDAO.getById(organizationId);
                if (organization != null) {
                    registers.add(new CheckoutRegister(id, new BuyList(), WorkshiftDAO.getById(1), organization));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registers;
    }

    public static void saveBuyList(BuyList buyList) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
