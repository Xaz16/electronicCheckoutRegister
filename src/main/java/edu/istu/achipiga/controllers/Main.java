package edu.istu.achipiga.controllers;

import edu.istu.achipiga.CheckoutRegister;
import edu.istu.achipiga.ElectronicCheckoutRegister;
import edu.istu.achipiga.dao.CheckoutRegisterDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private TreeView<String> navTree;
    
    @FXML
    public void initialize() {
        // Initialize checkout register
        CheckoutRegisterDAO.getCurrentCheckoutRegister();
        
        setupNavigation();
    }
    
    private void setupNavigation() {
        // Create root item
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);
        
        // Create navigation items
        TreeItem<String> products = new TreeItem<>("Товары");
        TreeItem<String> checkout = new TreeItem<>("Касса");
        TreeItem<String> receipts = new TreeItem<>("Чеки");
        TreeItem<String> info = new TreeItem<>("Информация");
        TreeItem<String> exit = new TreeItem<>("Выход");
        
        // Add items to root
        root.getChildren().addAll(products, checkout, receipts, info, exit);
        
        // Set the root
        navTree.setRoot(root);
        
        // Add selection listener
        navTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue.getValue()) {
                    case "Товары" -> showProducts();
                    case "Касса" -> showCheckout();
                    case "Чеки" -> showReports();
                    case "Информация" -> showInfo();
                    case "Выход" -> onExit();
                }
            }
        });
        
        // Select first item by default
        navTree.getSelectionModel().select(products);
    }
    
    private void showProducts() {
        loadView("Products.fxml");
    }
    
    private void showCheckout() {
        loadView("Checkout.fxml");
    }
    
    private void showReports() {
        loadView("Receipts.fxml");
    }
    
    private void showInfo() {
        loadView("Info.fxml");
    }
    
    private void onExit() {
        Stage stage = (Stage) navTree.getScene().getWindow();
        stage.close();
    }
    
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(ElectronicCheckoutRegister.class.getResource("views/" + fxml));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}