package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;
import java.util.List;

public class WorkshiftDAO extends BaseDAO<Workshift> {
    @Override
    public Workshift getById(int id) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM workshifts WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                return null;
            }
            
            int employeeId = rs.getInt("employee_id");
            String employeeSql = "SELECT * FROM employees WHERE id = ?";
            PreparedStatement employeeStmt = conn.prepareStatement(employeeSql);
            employeeStmt.setInt(1, employeeId);
            ResultSet employeeRs = employeeStmt.executeQuery();
            
            if (!employeeRs.next()) {
                return null;
            }
            
            Employee employee = new Employee(
                employeeRs.getInt("id"),
                employeeRs.getString("name"),
                employeeRs.getString("position")
            );
            
            return new Workshift(
                id,
                employee,
                rs.getString("start_time"),
                rs.getString("name")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    List<Workshift> getAll() {
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    Workshift save(Workshift instance) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    void delete(Workshift instance) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    Workshift getCurrent() {
        throw new UnsupportedOperationException("Unimplemented method 'getCurrent'");
    }
} 