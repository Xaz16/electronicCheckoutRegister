package edu.istu.achipiga.dao;

public class DAOFactory {
    private static DAOFactory instance;
    
    private final CustomerDAO customerDAO;
    private final ReceiptDAO receiptDAO;
    private final ProductDAO productDAO;
    private final BuyListDAO buyListDAO;
    private final CheckoutRegisterDAO checkoutRegisterDAO;
    private final OrganizationDAO organizationDAO;
    private final WorkshiftDAO workshiftDAO;

    private DAOFactory() {
        customerDAO = new CustomerDAO();
        receiptDAO = new ReceiptDAO();
        productDAO = new ProductDAO();
        buyListDAO = new BuyListDAO();
        checkoutRegisterDAO = new CheckoutRegisterDAO();
        organizationDAO = new OrganizationDAO();
        workshiftDAO = new WorkshiftDAO();
    }

    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }

    public CustomerDAO getCustomerDAO() {
        return customerDAO;
    }

    public ReceiptDAO getReceiptDAO() {
        return receiptDAO;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public BuyListDAO getBuyListDAO() {
        return buyListDAO;
    }

    public CheckoutRegisterDAO getCheckoutRegisterDAO() {
        return checkoutRegisterDAO;
    }

    public OrganizationDAO getOrganizationDAO() {
        return organizationDAO;
    }

    public WorkshiftDAO getWorkshiftDAO() {
        return workshiftDAO;
    }
} 