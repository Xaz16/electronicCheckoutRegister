package edu.istu.achipiga.controllers;

import edu.istu.achipiga.CheckoutRegister;
import edu.istu.achipiga.dao.CheckoutRegisterDAO;
import edu.istu.achipiga.services.ViewsService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.io.IOException;

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
            Parent view = ViewsService.getInstance().loadViewAndGetRoot(fxmlPath);
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.out.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
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
        loadView("/edu/istu/achipiga/views/Receipts.fxml");
    }

    @FXML
    private void showInfo() {
        loadView("/edu/istu/achipiga/views/Info.fxml");
    }
}