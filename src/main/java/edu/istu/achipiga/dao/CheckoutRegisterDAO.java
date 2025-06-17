package edu.istu.achipiga.dao;

import edu.istu.achipiga.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckoutRegisterDAO extends BaseDAO<CheckoutRegister> {
    WorkshiftDAO workshiftDAO = new WorkshiftDAO();
    OrganizationDAO organizationDAO = new OrganizationDAO();

    @Override
    public CheckoutRegister getCurrentInstance() {
        return super.getCurrentInstance();
    }

    @Override
    public void setCurrentInstance(CheckoutRegister instance) {
        super.setCurrentInstance(instance);
    }

    @Override
    public CheckoutRegister getCurrent() {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM checkout_registers ORDER BY id DESC LIMIT 1";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            if (!rs.next()) {
                return null;
            }
            
            int id = rs.getInt("id");
            int organizationId = rs.getInt("organization_id");
            
            Organization organization = organizationDAO.getById(organizationId);
            if (organization == null) {
                return null;
            }

            setCurrentInstance(new CheckoutRegister(id, new BuyList(), workshiftDAO.getById(1), organization));
            return getCurrentInstance();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CheckoutRegister getById(int id) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM checkout_registers WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                return null;
            }
            
            int organizationId = rs.getInt("organization_id");
            Organization organization = organizationDAO.getById(organizationId);
            
            if (organization == null) {
                return null;
            }
            
            return new CheckoutRegister(id, new BuyList(), workshiftDAO.getById(1), organization);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;        
        }
    }

    @Override
    public List<CheckoutRegister> getAll() {
        List<CheckoutRegister> registers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM checkout_registers";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int organizationId = rs.getInt("organization_id");
                
                Organization organization = organizationDAO.getById(organizationId);    
                if (organization != null) {
                    registers.add(new CheckoutRegister(id, new BuyList(), workshiftDAO.getById(1), organization));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registers;
    }

    @Override
    public CheckoutRegister save(CheckoutRegister instance) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public void delete(CheckoutRegister instance) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
