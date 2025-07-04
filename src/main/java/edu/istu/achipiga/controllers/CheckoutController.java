package edu.istu.achipiga.controllers;

import edu.istu.achipiga.*;
import edu.istu.achipiga.dao.DAOFactory;
import edu.istu.achipiga.services.ViewsService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class CheckoutController {
    @FXML
    private TableView<edu.istu.achipiga.BuyListItem> cartTable;
    @FXML private TableColumn<edu.istu.achipiga.BuyListItem, Integer> indexColumn;
    @FXML private TableColumn<edu.istu.achipiga.BuyListItem, String> productColumn;
    @FXML private TableColumn<edu.istu.achipiga.BuyListItem, BigDecimal> priceColumn;
    @FXML private TableColumn<edu.istu.achipiga.BuyListItem, Integer> quantityColumn;
    @FXML private TableColumn<edu.istu.achipiga.BuyListItem, BigDecimal> sumColumn;

    @FXML private Label totalSumLabel;
    @FXML private Label discountLabel;
    @FXML private Label finalSumLabel;
    @FXML private Button proceedToPaymentButton;
    @FXML private Button applyDiscountButton;
    @FXML private Button removeSelectedButton;
    @FXML private Button clearCartButton;

    private BigDecimal discount = BigDecimal.ZERO;
    private boolean discountApplied = false;
    private static CheckoutRegister checkoutRegister = DAOFactory.getInstance().getCheckoutRegisterDAO().getCurrent();
    private StackPane contentArea;

    @FXML
    public void initialize() {
        setupTableColumns();
        cartTable.setItems(FXCollections.observableArrayList(checkoutRegister.getBuyList().getItems()));
        updateTotals();
        updateButtonsState();
        
        cartTable.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                contentArea = (StackPane) newVal.lookup("#contentArea");
            }
        });

        cartTable.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends edu.istu.achipiga.BuyListItem> change) -> {
            updateButtonsState();
        });

        cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateButtonsState();
        });
    }

    private void setupTableColumns() {
        indexColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cartTable.getItems().indexOf(cellData.getValue()) + 1));

        productColumn.setCellValueFactory(cellData -> 
                new ReadOnlyObjectWrapper<>(cellData.getValue().getProduct().getName()));
        priceColumn.setCellValueFactory(cellData -> 
                new ReadOnlyObjectWrapper<>(cellData.getValue().getProduct().getPrice()));
        quantityColumn.setCellValueFactory(cellData -> 
                new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity()));
        sumColumn.setCellValueFactory(cellData -> 
                new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalSum()));

        priceColumn.setCellFactory(col -> new TableCell<edu.istu.achipiga.BuyListItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : formatCurrency(price));
            }
        });

        sumColumn.setCellFactory(col -> new TableCell<edu.istu.achipiga.BuyListItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal sum, boolean empty) {
                super.updateItem(sum, empty);
                setText(empty ? null : formatCurrency(sum));
            }
        });
    }

    public static void addProductToCart(Product product) {
        Optional<edu.istu.achipiga.BuyListItem> existingItem = checkoutRegister.getBuyList().getByProduct(product.getId());
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
        } else {
            checkoutRegister.getBuyList().addItem(new edu.istu.achipiga.BuyListItem(product, 1));
        }
    }

    @FXML
    private void removeSelected() {
        edu.istu.achipiga.BuyListItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            checkoutRegister.getBuyList().removeItem(selected);
            cartTable.getItems().remove(selected);
            updateTotals();
            updateButtonsState();
        }
    }

    @FXML
    private void clearCart() {
        checkoutRegister.getBuyList().getItems().clear();
        cartTable.getItems().clear();
        discount = BigDecimal.ZERO;
        discountApplied = false;
        updateTotals();
        updateButtonsState();
    }

    @FXML
    private void applyDiscount() {
        Customer customer = DAOFactory.getInstance().getCustomerDAO().getCurrent();
        discount = customer.getDiscountCard().getDiscount();
        discountApplied = true;
        updateTotals();
    }

    @FXML
    private void proceedToPayment() {
        if (checkoutRegister.getBuyList().getItems().isEmpty()) return;

        showPaymentScreen();
    }

    private void updateTotals() {
        BigDecimal total = calculateTotal();
        BigDecimal finalSum = calculateFinalSum(total);

        totalSumLabel.setText(formatCurrency(total));
        discountLabel.setText("-" + formatCurrency(total.multiply(discount)));
        finalSumLabel.setText(formatCurrency(finalSum));
    }

    private BigDecimal calculateTotal() {
        return checkoutRegister.getBuyList().getTotalSum();
    }

    private BigDecimal calculateFinalSum(BigDecimal total) {
        return total.subtract(total.multiply(discount))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f руб.", amount);
    }

    public BigDecimal getFinalSum() {
        return calculateFinalSum(calculateTotal());
    }

    private void showPaymentScreen() {
        if (contentArea == null) {
            contentArea = (StackPane) cartTable.getScene().lookup("#contentArea");
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/istu/achipiga/views/Payment.fxml"));
            Parent paymentView = loader.load();
            PaymentController paymentController = loader.getController();
            
            paymentController.setup(getFinalSum(), checkoutRegister, new PaymentController.PaymentCallback() {
                @Override
                public void onPaymentSuccess(Receipt receipt) {
                    BigDecimal total = calculateTotal();
                    BigDecimal discountAmount = discountApplied ? total.multiply(discount) : BigDecimal.ZERO;

                    receipt.setTotalAmount(total);
                    receipt.setDiscountAmount(discountAmount);

                    DAOFactory.getInstance().getBuyListDAO().save(checkoutRegister.getBuyList());
                    saveReceipt(receipt);
                    clearCart();

                    try {
                        Parent view = ViewsService.getInstance().loadViewAndGetRoot("/edu/istu/achipiga/views/Checkout.fxml");
                        contentArea.getChildren().setAll(view);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPaymentCancel() {
                    try {
                        Parent view = ViewsService.getInstance().loadViewAndGetRoot("/edu/istu/achipiga/views/Checkout.fxml");
                        contentArea.getChildren().setAll(view);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            paymentController.setDiscountInfo(discountApplied, discount);

            contentArea.getChildren().setAll(paymentView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveReceipt(Receipt receipt) {
        Receipt savedReceipt = DAOFactory.getInstance().getReceiptDAO().save(receipt);
        if (savedReceipt == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось сохранить чек");
            alert.setContentText("Пожалуйста, попробуйте еще раз.");
            alert.showAndWait();
            return;
        }

        receipt.setId(savedReceipt.getId());
        showPaymentSuccess(receipt);
    }

    private void showPaymentSuccess(Receipt receipt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        
        String formattedTime = java.time.Instant.parse(receipt.time)
            .atZone(java.time.ZoneId.systemDefault())
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
            
        alert.setTitle("Оплата прошла успешно");
        alert.setHeaderText(String.format("Чек #%d от %s", receipt.getId(), formattedTime));
        
        StringBuilder content = new StringBuilder();
        content.append(String.format("Тип чека: %s\n", receipt.getReceiptType().getLabel()));
        content.append(String.format("Покупатель: %s\n", receipt.getCustomer().getName()));
        content.append(String.format("Сумма покупки (до скидки): %,.2f руб.\n", receipt.getTotalAmount()));
        content.append(String.format("Скидка: %,.2f руб.\n", receipt.getDiscountAmount()));
        content.append(String.format("Итого к оплате: %,.2f руб.\n", receipt.getFinalSum()));
        content.append(String.format("Внесено: %,.2f руб.\n", receipt.getProvidedSum()));
        content.append(String.format("Сдача: %,.2f руб.\n", receipt.getExchange()));
        content.append(String.format("Способ оплаты: %s\n", receipt.getPaymentMethod().getLabel()));
        content.append(String.format("\nОрганизация: %s", 
            receipt.getCheckoutRegister().getOrganization().name));
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void updateButtonsState() {
        boolean hasItems = !checkoutRegister.getBuyList().getItems().isEmpty();
        boolean hasSelection = cartTable.getSelectionModel().getSelectedItem() != null;
        
        proceedToPaymentButton.setDisable(!hasItems);
        applyDiscountButton.setDisable(!hasItems);
        clearCartButton.setDisable(!hasItems);
        removeSelectedButton.setDisable(!hasSelection);
    }
}
