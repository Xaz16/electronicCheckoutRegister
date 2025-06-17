package edu.istu.achipiga.controllers;

import edu.istu.achipiga.Product;
import edu.istu.achipiga.dao.ProductDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigInteger;

public class ProductsController {

    @FXML
    private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, BigInteger> priceColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Void> actionColumn;
    @FXML private TextField searchField;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Добавить");

            {
                btn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    CheckoutController.addProductToCart(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            productsTable.setItems(ProductDAO.getAllProducts(newValue));
        });

        productsTable.setItems(ProductDAO.getAllProducts(""));
    }
}