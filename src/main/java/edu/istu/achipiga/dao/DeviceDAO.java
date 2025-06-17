package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;

public class DeviceDAO {
    public static Device getById(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            String sql = "SELECT * FROM devices WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                return null;
            }
            
            String type = rs.getString("type");
            Device device;
            
            switch (type) {
                case "scanner":
                    device = new ScanCodesScanner();
                    break;
                case "printer":
                    device = new CashReceiptPrinter();
                    break;
                default:
                    return null;
            }
            
            device.name = rs.getString("name");
            return device;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
} 