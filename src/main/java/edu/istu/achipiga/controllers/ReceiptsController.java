package edu.istu.achipiga.controllers;

import edu.istu.achipiga.Receipt;
import edu.istu.achipiga.dao.ReceiptDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ReceiptsController {
    @FXML private TableView<Receipt> receiptsTable;
    @FXML private TableColumn<Receipt, Integer> idColumn;
    @FXML private TableColumn<Receipt, String> timeColumn;
    @FXML private TableColumn<Receipt, String> typeColumn;
    @FXML private TableColumn<Receipt, String> customerColumn;
    @FXML private TableColumn<Receipt, String> paymentMethodColumn;
    @FXML private TableColumn<Receipt, BigDecimal> paidSumColumn;
    @FXML private TableColumn<Receipt, BigDecimal> exchangeColumn;
    @FXML private TableColumn<Receipt, BigDecimal> discountColumn;
    @FXML private TableColumn<Receipt, BigDecimal> finalColumn;
    @FXML private TableColumn<Receipt, BigDecimal> totalColumn;
    @FXML private Button viewDetailsButton;

    private ObservableList<Receipt> receipts = FXCollections.observableArrayList();
    private StackPane contentArea;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadReceipts();
        
        
        receiptsTable.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                contentArea = (StackPane) newVal.lookup("#contentArea");
            }
        });

        
        receiptsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            viewDetailsButton.setDisable(newVal == null);
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        timeColumn.setCellValueFactory(cellData -> {
            String time = cellData.getValue().getTime();
            Instant instant = Instant.parse(time);
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return new ReadOnlyObjectWrapper<>(dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        });
        
        typeColumn.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getReceiptType().getLabel()));
        
        customerColumn.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getCustomer().getName()));
        
        paymentMethodColumn.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getPaymentMethod().getLabel()));
        
        paidSumColumn.setCellValueFactory(new PropertyValueFactory<>("providedSum"));
        paidSumColumn.setCellFactory(col -> new TableCell<Receipt, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty ? null : formatCurrency(amount));
            }
        });
        
        exchangeColumn.setCellValueFactory(cellData -> {
            BigDecimal exchange = cellData.getValue().getExchange();
            return new ReadOnlyObjectWrapper<>(exchange);
        });
        exchangeColumn.setCellFactory(col -> new TableCell<Receipt, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty ? null : formatCurrency(amount));
            }
        });
        
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));
        discountColumn.setCellFactory(col -> new TableCell<Receipt, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty ? null : formatCurrency(amount));
            }
        });
        
        finalColumn.setCellValueFactory(cellData -> {
            BigDecimal finalSum = cellData.getValue().getFinalSum();
            return new ReadOnlyObjectWrapper<>(finalSum);
        });
        
        finalColumn.setCellFactory(col -> new TableCell<Receipt, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty ? null : formatCurrency(amount));
            }
        });

        totalColumn.setCellValueFactory(cellData -> {
            BigDecimal total = cellData.getValue().getTotalAmount();
            return new ReadOnlyObjectWrapper<>(total);
        });

        totalColumn.setCellFactory(col -> new TableCell<Receipt, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty ? null : formatCurrency(amount));
            }
        });
    }
    public void loadReceipts() {
        receipts = FXCollections.observableArrayList(ReceiptDAO.loadReceipts());
        receiptsTable.setItems(receipts);
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%.2f ₽", amount);
    }

    @FXML
    private void onViewDetails() {
        Receipt selectedReceipt = receiptsTable.getSelectionModel().getSelectedItem();
        if (selectedReceipt != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/istu/achipiga/views/Receipt.fxml"));
                Parent receiptView = loader.load();
                ReceiptController receiptController = loader.getController();
                receiptController.setReceipt(selectedReceipt);
                
                if (contentArea != null) {
                    contentArea.getChildren().setAll(receiptView);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
} 