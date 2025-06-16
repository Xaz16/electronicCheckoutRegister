package edu.istu.achipiga.controllers;

import edu.istu.achipiga.CheckoutRegister;
import edu.istu.achipiga.Organization;
import edu.istu.achipiga.Product;
import edu.istu.achipiga.dao.CheckoutRegisterDAO;
import edu.istu.achipiga.dao.ProductDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigInteger;

public class Products {

    @FXML
    private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, BigInteger> priceColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Void> actionColumn;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        // Колонка с кнопкой (самая простая реализация)
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Добавить");

            {
                btn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    Checkout.addProductToCart(product);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Загрузка данных
        productsTable.setItems(ProductDAO.getAllProducts());
    }
}