package edu.istu.achipiga.controllers;

import edu.istu.achipiga.Receipt;
import edu.istu.achipiga.BuyListItem;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;

public class ReceiptController {
    @FXML private Label idLabel;
    @FXML private Label timeLabel;
    @FXML private Label typeLabel;
    @FXML private Label customerLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label cardNumberLabel;
    @FXML private Label cardNumberValue;
    @FXML private Label paidSumLabel;
    @FXML private Label discountLabel;
    @FXML private Label exchangeLabel;
    @FXML private Label totalLabel;
    @FXML private Label finalSumLabel;
    @FXML private TableView<BuyListItem> itemsTable;
    @FXML private TableColumn<BuyListItem, String> productColumn;
    @FXML private TableColumn<BuyListItem, BigDecimal> priceColumn;
    @FXML private TableColumn<BuyListItem, Integer> quantityColumn;
    @FXML private TableColumn<BuyListItem, BigDecimal> sumColumn;
    
    @FXML private Button backButton;

    private Receipt receipt;
    private ObservableList<BuyListItem> items = FXCollections.observableArrayList();
    private StackPane contentArea;

    @FXML
    public void initialize() {
        setupTableColumns();
        
        
        itemsTable.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                contentArea = (StackPane) newVal.lookup("#contentArea");
            }
        });
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
        updateUI();
    }

    private void setupTableColumns() {
        productColumn.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getProduct().getName()));
        
        priceColumn.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getBoughtPrice()));
        priceColumn.setCellFactory(col -> new TableCell<BuyListItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : formatCurrency(price));
            }
        });
        
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        sumColumn.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalSum()));
        sumColumn.setCellFactory(col -> new TableCell<BuyListItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal sum, boolean empty) {
                super.updateItem(sum, empty);
                setText(empty ? null : formatCurrency(sum));
            }
        });
    }

    private void updateUI() {
        if (receipt == null) return;

        idLabel.setText(String.valueOf(receipt.getId()));
        
        Instant instant = Instant.parse(receipt.getTime());
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        timeLabel.setText(dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        
        typeLabel.setText(receipt.getReceiptType().getLabel());
        customerLabel.setText(receipt.getCustomer().getName());
        paymentMethodLabel.setText(receipt.getPaymentMethod().getLabel());

        cardNumberLabel.setVisible(receipt.getBankCard() != null);
        cardNumberValue.setVisible(receipt.getBankCard() != null);

        if(receipt.getBankCard() != null) {
            cardNumberValue.setText("**** **** **** " + receipt.getBankCard().getNumber().substring(12));
        }

        paidSumLabel.setText(formatCurrency(receipt.getProvidedSum()));
        discountLabel.setText(formatCurrency(receipt.getDiscountAmount()));
        
        exchangeLabel.setText(formatCurrency(receipt.getExchange()));
        
        totalLabel.setText(formatCurrency(receipt.getTotalAmount()));
        finalSumLabel.setText(formatCurrency(receipt.getFinalSum()));

        
        items.setAll(receipt.getBuyList().getItems());
        itemsTable.setItems(items);
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%.2f ₽", amount);
    }

    @FXML
    private void onBack() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/edu/istu/achipiga/views/Receipts.fxml"));
            if (contentArea != null) {
                contentArea.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 