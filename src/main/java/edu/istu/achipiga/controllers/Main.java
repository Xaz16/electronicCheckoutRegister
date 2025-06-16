package edu.istu.achipiga.controllers;

import edu.istu.achipiga.CheckoutRegister;
import edu.istu.achipiga.dao.CheckoutRegisterDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;

public class Main {
    @FXML
    private StackPane contentArea;
    @FXML
    private Label workshiftLabel;
    @FXML
    private Label employeeLabel;

    private CheckoutRegister checkoutRegister;

    public void initialize() {
        checkoutRegister = CheckoutRegisterDAO.getCurrentCheckoutRegister();

        workshiftLabel.textProperty().bind(new SimpleStringProperty(checkoutRegister.getWorkshift().getName()));
        employeeLabel.textProperty().bind(new SimpleStringProperty(checkoutRegister.getWorkshift().getEmployee().getName()));
        showCheckout();
    }

    private void loadView(String fxmlPath) {
        try {
            // Try to get resource from multiple possible locations
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.out.println("FXML file not found at path: " + fxmlPath);
                return;
            }

            Node view = FXMLLoader.load(resource);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.out.println("Error loading FXML file: " + e.getMessage());
            System.out.println("Stack trace: " + e.getStackTrace()[0]);
            System.out.println(e);
        }
    }

    @FXML
    private void showProducts() {
        loadView("/edu/istu/achipiga/views/Products.fxml");
    }


    @FXML
    private void showCheckout() {
        loadView("/edu/istu/achipiga/views/Checkout.fxml");
    }

    @FXML
    private void showReports() {
        loadView("/edu/istu/achipiga/views/Reports.fxml");
    }

    @FXML
    private void showInfo() {
        loadView("/edu/istu/achipiga/views/Info.fxml");
    }
}