package edu.istu.achipiga.dao;

import edu.istu.achipiga.Employee;
import edu.istu.achipiga.Location;
import edu.istu.achipiga.Organization;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrganizationDAO extends BaseDAO<Organization> {
    @Override
    public Organization getCurrent() {
        if(super.getCurrentInstance() != null) return super.getCurrentInstance();
        try (Connection conn = getConnection()) {
            ResultSet orgRs = conn.createStatement().executeQuery("SELECT * FROM organizations WHERE id = 1");
            ResultSet locationRs = conn.createStatement().executeQuery("SELECT * FROM locations WHERE organization_id = 1");
            ResultSet employeeRs = conn.createStatement().executeQuery("SELECT * FROM employees WHERE organization_id = 1");

            List<Employee> employees = new ArrayList<Employee>();
            Employee boss = null;

            while(employeeRs.next()) {
                if(Objects.equals(orgRs.getString("boss_id"), employeeRs.getString("id"))) {
                    boss = new Employee(
                            employeeRs.getInt("id"),
                            employeeRs.getString("name"),
                            employeeRs.getString("position")
                    );
                }


                employees.add(new Employee(
                        employeeRs.getInt("id"),
                        employeeRs.getString("name"),
                        employeeRs.getString("position")
                ));

            }

            super.setCurrentInstance(new Organization(
                    orgRs.getString("id"),
                    orgRs.getString("name"),
                    employees,
                    new Location(
                            locationRs.getString("country"),
                            locationRs.getString("city"),
                            locationRs.getString("street"),
                            locationRs.getString("build")
                    ),
                    orgRs.getString("inn"),
                    boss
            ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return super.getCurrentInstance();
    }

    @Override
    public Organization getById(int id) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM organizations WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet orgRs = pstmt.executeQuery();
            
            if (!orgRs.next()) {
                return null;
            }
            
            String locationSql = "SELECT * FROM locations WHERE organization_id = ?";
            PreparedStatement locationStmt = conn.prepareStatement(locationSql);
            locationStmt.setInt(1, id);
            ResultSet locationRs = locationStmt.executeQuery();
            
            if (!locationRs.next()) {
                return null;
            }
            
            String employeeSql = "SELECT * FROM employees WHERE organization_id = ?";
            PreparedStatement employeeStmt = conn.prepareStatement(employeeSql);
            employeeStmt.setInt(1, id);
            ResultSet employeeRs = employeeStmt.executeQuery();
            
            List<Employee> employees = new ArrayList<>();
            Employee boss = null;
            
            while (employeeRs.next()) {
                Employee employee = new Employee(
                    employeeRs.getInt("id"),
                    employeeRs.getString("name"),
                    employeeRs.getString("position")
                );
                
                if (Objects.equals(orgRs.getString("boss_id"), employeeRs.getString("id"))) {
                    boss = employee;
                }
                
                employees.add(employee);
            }
            
            return new Organization(
                orgRs.getString("id"),
                orgRs.getString("name"),
                employees,
                new Location(
                    locationRs.getString("country"),
                    locationRs.getString("city"),
                    locationRs.getString("street"),
                    locationRs.getString("build")
                ),
                orgRs.getString("inn"),
                boss
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    void delete(Organization instance) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    List<Organization> getAll() {
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    Organization save(Organization instance) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
    
}
