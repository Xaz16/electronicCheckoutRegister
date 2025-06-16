package edu.istu.achipiga.dao;

import edu.istu.achipiga.Employee;
import edu.istu.achipiga.Location;
import edu.istu.achipiga.Organization;
import edu.istu.achipiga.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OrganizationDAO {
    private static Organization curr = null;

    public static Organization getCurrentOrganization() {
        if(curr != null) return curr;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
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

            curr = new Organization(
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
        }
        return curr;
    }
}
