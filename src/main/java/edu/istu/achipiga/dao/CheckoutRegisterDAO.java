package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.math.BigDecimal;

public class CheckoutRegisterDAO {
    private static CheckoutRegister curr = null;

    public static CheckoutRegister getCurrentCheckoutRegister() {
        if(curr != null) return curr;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            Organization org = OrganizationDAO.getCurrentOrganization();
            ResultSet checkoutRs = conn.createStatement().executeQuery("SELECT * FROM checkout_registers WHERE organization_id = 1");
            ResultSet workshiftRs = conn.createStatement().executeQuery("SELECT * FROM workshifts WHERE employee_id = 6");
            ResultSet employeeRs = conn.createStatement().executeQuery("SELECT * FROM employees WHERE id = 6");

            curr = new CheckoutRegister(
                    checkoutRs.getInt("id"),
                    new ArrayList<>(),
                    new BuyList(),
                    new Workshift(
                            workshiftRs.getInt("id"),
                            new Employee(
                                    employeeRs.getInt("id"),
                                    employeeRs.getString("name"),
                                    employeeRs.getString("position")
                            ),
                            workshiftRs.getString("start_time"),
                            workshiftRs.getString("name")
                    ),
                    org
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return curr;
    }

    public static CheckoutRegister getById(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            // Get checkout register data
            String checkoutSQL = "SELECT * FROM checkout_registers WHERE id = ?";
            PreparedStatement checkoutStmt = conn.prepareStatement(checkoutSQL);
            checkoutStmt.setInt(1, id);
            ResultSet checkoutRs = checkoutStmt.executeQuery();
            
            if (!checkoutRs.next()) {
                System.out.println("Checkout register not found");
                return null;
            }
            
            // Get organization data
            String orgSQL = "SELECT * FROM organizations WHERE id = ?";
            PreparedStatement orgStmt = conn.prepareStatement(orgSQL);
            orgStmt.setInt(1, checkoutRs.getInt("organization_id"));
            ResultSet orgRs = orgStmt.executeQuery();
            
            if (!orgRs.next()) {
                System.out.println("Organization not found");
                return null;
            }
            
            // Get location data
            String locationSQL = "SELECT * FROM locations WHERE id = ?";
            PreparedStatement locationStmt = conn.prepareStatement(locationSQL);
            locationStmt.setInt(1, checkoutRs.getInt("location_id"));
            ResultSet locationRs = locationStmt.executeQuery();
            
            if (!locationRs.next()) {
                System.out.println("Location not found");
                return null;
            }
            
            // Get boss data
            String bossSQL = "SELECT * FROM employees WHERE id = ?";
            PreparedStatement bossStmt = conn.prepareStatement(bossSQL);
            bossStmt.setInt(1, orgRs.getInt("boss_id"));
            ResultSet bossRs = bossStmt.executeQuery();
            
            if (!bossRs.next()) {
                System.out.println("Boss not found");
                return null;
            }
            
            // Get all employees
            String employeesSQL = "SELECT * FROM employees WHERE organization_id = ?";
            PreparedStatement employeesStmt = conn.prepareStatement(employeesSQL);
            employeesStmt.setInt(1, orgRs.getInt("id"));
            ResultSet employeesRs = employeesStmt.executeQuery();
            
            List<Employee> employees = new ArrayList<>();
            while (employeesRs.next()) {
                employees.add(new Employee(
                    employeesRs.getInt("id"),
                    employeesRs.getString("name"),
                    employeesRs.getString("position")
                ));
            }
            
            // Create organization
            Organization organization = new Organization(
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
                new Employee(
                    bossRs.getInt("id"),
                    bossRs.getString("name"),
                    bossRs.getString("position")
                )
            );
            
            // Get buy list data
            String buyListSQL = "SELECT * FROM buy_lists WHERE id = (SELECT buy_list_id FROM receipts WHERE checkout_register_id = ? ORDER BY id DESC LIMIT 1)";
            PreparedStatement buyListStmt = conn.prepareStatement(buyListSQL);
            buyListStmt.setInt(1, id);
            ResultSet buyListRs = buyListStmt.executeQuery();
            
            BuyList buyList = new BuyList();
            if (buyListRs.next()) {
                // Get buy list items
                String itemsSQL = "SELECT * FROM buy_list_items WHERE buy_lists = ?";
                PreparedStatement itemsStmt = conn.prepareStatement(itemsSQL);
                itemsStmt.setInt(1, buyListRs.getInt("id"));
                ResultSet itemsRs = itemsStmt.executeQuery();
                
                while (itemsRs.next()) {
                    // Get product data
                    String productSQL = "SELECT * FROM products WHERE id = ?";
                    PreparedStatement productStmt = conn.prepareStatement(productSQL);
                    productStmt.setInt(1, itemsRs.getInt("product_id"));
                    ResultSet productRs = productStmt.executeQuery();
                    
                    if (productRs.next()) {
                        Product product = new Product(
                            productRs.getString("id"),
                            productRs.getString("name"),
                            productRs.getString("category"),
                            BigDecimal.valueOf(productRs.getLong("price"))
                        );
                        buyList.addItem(new BuyListItem(product, itemsRs.getInt("quantity")));
                    }
                }
            }
            
            // Use the current workshift since it's not stored in the database
            return new CheckoutRegister(
                checkoutRs.getInt("id"),
                new ArrayList<>(), // Empty devices list for now
                buyList,
                curr.getWorkshift(), // Use current workshift
                organization
            );
            
        } catch (SQLException e) {
            System.out.println("Error getting checkout register: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBuyList(BuyList buyList) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:checkout_register.db")) {
            // Insert buy list
            String insertBuyListSQL = "INSERT INTO buy_lists (totalSum) VALUES (?)";
            PreparedStatement buyListStmt = conn.prepareStatement(insertBuyListSQL, Statement.RETURN_GENERATED_KEYS);
            buyListStmt.setLong(1, buyList.getTotalSum().longValue());
            buyListStmt.executeUpdate();

            // Get the generated buy list ID
            ResultSet generatedKeys = buyListStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                System.out.println("Failed to get generated buy list ID");
                return;
            }
            int buyListId = generatedKeys.getInt(1);

            // Insert buy list items
            String insertItemsSQL = "INSERT INTO buy_list_items (quantity, bought_price, buy_lists, product_id) VALUES (?, ?, ?, ?)";
            PreparedStatement itemsStmt = conn.prepareStatement(insertItemsSQL);
            
            for (BuyListItem item : buyList.getItems()) {
                itemsStmt.setInt(1, item.getQuantity());
                itemsStmt.setLong(2, item.getProduct().getPrice().longValue());
                itemsStmt.setInt(3, buyListId);
                itemsStmt.setInt(4, Integer.parseInt(item.getProduct().getId()));
                itemsStmt.executeUpdate();
            }

            // Update the buy list ID
            buyList.setId(buyListId);
        } catch (SQLException e) {
            System.out.println("Error saving buy list: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
