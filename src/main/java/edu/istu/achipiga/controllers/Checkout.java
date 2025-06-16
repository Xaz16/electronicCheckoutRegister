package edu.istu.achipiga.controllers;

import edu.istu.achipiga.*;
import edu.istu.achipiga.dao.CheckoutRegisterDAO;
import edu.istu.achipiga.dao.CustomerDAO;
import edu.istu.achipiga.dao.ReceiptDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Checkout {

    @FXML
    private TableView<BuyListItem> cartTable;
    @FXML private TableColumn<BuyListItem, Integer> indexColumn;
    @FXML private TableColumn<BuyListItem, String> productColumn;
    @FXML private TableColumn<BuyListItem, BigDecimal> priceColumn;
    @FXML private TableColumn<BuyListItem, Integer> quantityColumn;
    @FXML private TableColumn<BuyListItem, BigDecimal> sumColumn;

    @FXML private Label totalSumLabel;
    @FXML private Label discountLabel;
    @FXML private Label finalSumLabel;

    @Getter
    public static ObservableList<BuyListItem> BuyListItems = FXCollections.observableArrayList();
    private BigDecimal discount = BigDecimal.ZERO;
    private static CheckoutRegister checkoutRegister = CheckoutRegisterDAO.getCurrentCheckoutRegister();


    @FXML
    public void initialize() {
        setupTableColumns();
        cartTable.setItems(BuyListItems);
        updateTotals();

        BuyListItems.addListener(new ListChangeListener<BuyListItem>() {
            public void onChanged(Change<?extends BuyListItem> change) {

                while (change.next()) {
                    if (change.wasAdded()) {
                        BuyListItem changedItem = (BuyListItem) change.getAddedSubList().get(0);
                        edu.istu.achipiga.BuyListItem item = new edu.istu.achipiga.BuyListItem(changedItem.getProduct(), 1);
                        checkoutRegister.getBuyList().addItem(item);

                    }
                    if (change.wasRemoved()) {
                        checkoutRegister.getBuyList().removeItem(change.getFrom());
                    }

                    updateTotals();
                }
            }
        });
    }

    private void setupTableColumns() {
        indexColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cartTable.getItems().indexOf(cellData.getValue()) + 1));

        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));

        priceColumn.setCellFactory(col -> new TableCell<BuyListItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : formatCurrency(price));
            }
        });

        sumColumn.setCellFactory(col -> new TableCell<BuyListItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal sum, boolean empty) {
                super.updateItem(sum, empty);
                setText(empty ? null : formatCurrency(sum));
            }
        });
    }

    public static void addProductToCart(Product product) {
        for (BuyListItem item : BuyListItems) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                checkoutRegister.getBuyList().getByProduct(item.getProduct().getId()).get().setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        BuyListItem newItem = new BuyListItem(
                product,
                1
        );
        BuyListItems.add(newItem);
    }

    @FXML
    private void removeSelected() {
        BuyListItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            BuyListItems.remove(selected);
            updateTotals();
        }
    }

    @FXML
    private void clearCart() {
        BuyListItems.clear();
        discount = BigDecimal.ZERO;
        updateTotals();
    }

    @FXML
    private void applyDiscount() {
        Customer customer = CustomerDAO.getCurrentCustomer();
        discount = customer.getDiscountCard().getDiscount();
        updateTotals();
    }

    @FXML
    private void proceedToPayment() {
        if (BuyListItems.isEmpty()) return;

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/istu/achipiga/views/Payment.fxml"));
            Parent paymentView = loader.load();

            Payment paymentController = loader.getController();
            paymentController.setup(getFinalSum(), new Payment.PaymentCallback() {
                @Override
                public void onPaymentSuccess(Receipt receipt) {
                    // Обработка успешной оплаты
                    System.out.println("Оплата прошла: " + receipt);
                    saveReceipt(receipt);
                    clearCart();
                    showPaymentSuccess(receipt);
                }

                @Override
                public void onPaymentCancel() {
                    // Возврат в корзину
                    System.out.println("Оплата отменена");
                }
            });

            // Замена содержимого главного окна
            Stage stage = (Stage) cartTable.getScene().getWindow();
            stage.getScene().setRoot(paymentView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveReceipt(Receipt receipt) {
        ReceiptDAO.insertNew(receipt);
    }

    private void showPaymentSuccess(Receipt receipt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Оплата прошла успешно");
        alert.setHeaderText("Чек #" + receipt.getId());
        alert.setContentText(String.format(
                "Сумма: %,.2f руб.\nСпособ оплаты: %s\nСдача: %,.2f руб.",
                receipt.getAmount(),
                receipt.getPaymentMethodLabelString(),
                receipt.getChange()
        ));
        alert.showAndWait();
    }
}
