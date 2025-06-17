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
            int deviceId = rs.getInt("device_id");
            int buyListId = rs.getInt("buy_list_id");
            int workshiftId = rs.getInt("workshift_id");
            int organizationId = rs.getInt("organization_id");
            
            Device device = DeviceDAO.getById(deviceId);
            BuyList buyList = BuyListDAO.getById(buyListId);
            Workshift workshift = WorkshiftDAO.getById(workshiftId);
            Organization organization = OrganizationDAO.getById(organizationId);
            
            if (device == null || buyList == null || workshift == null || organization == null) {
                return null;
            }
            
            return new CheckoutRegister(id, device, buyList, workshift, organization);
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
            
            int deviceId = rs.getInt("device_id");
            int buyListId = rs.getInt("buy_list_id");
            int workshiftId = rs.getInt("workshift_id");
            int organizationId = rs.getInt("organization_id");
            
            Device device = DeviceDAO.getById(deviceId);
            BuyList buyList = BuyListDAO.getById(buyListId);
            Workshift workshift = WorkshiftDAO.getById(workshiftId);
            Organization organization = OrganizationDAO.getById(organizationId);
            
            if (device == null || buyList == null || workshift == null || organization == null) {
                return null;
            }
            
            return new CheckoutRegister(id, device, buyList, workshift, organization);
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
                int deviceId = rs.getInt("device_id");
                int buyListId = rs.getInt("buy_list_id");
                int workshiftId = rs.getInt("workshift_id");
                int organizationId = rs.getInt("organization_id");
                
                Device device = DeviceDAO.getById(deviceId);
                BuyList buyList = BuyListDAO.getById(buyListId);
                Workshift workshift = WorkshiftDAO.getById(workshiftId);
                Organization organization = OrganizationDAO.getById(organizationId);
                
                if (device != null && buyList != null && workshift != null && organization != null) {
                    registers.add(new CheckoutRegister(id, device, buyList, workshift, organization));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registers;
    }
}
