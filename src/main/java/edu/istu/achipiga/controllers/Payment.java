package edu.istu.achipiga.controllers;

import edu.istu.achipiga.*;
import edu.istu.achipiga.dao.CustomerDAO;
import edu.istu.achipiga.dao.ReceiptDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class Payment implements Initializable {
    @FXML
    private Label totalSumLabel;
    @FXML
    private Label cardNumberLabel;
    @FXML
    private TextField cardNumberField;
    @FXML
    private Label cashAmountLabel;
    @FXML
    private TextField cashAmountField;
    @FXML
    private Label changeLabel;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private Button payButton;
    @FXML
    private Button backButton;

    private CheckoutRegister checkoutRegister;
    private BuyList buyList;
    private PaymentCallback callback;
    private boolean discountApplied = false;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal amountToPay = BigDecimal.ZERO;

    public interface PaymentCallback {
        void onPaymentSuccess(Receipt receipt);
        void onPaymentCancel();
    }

    public void setup(BigDecimal amount, CheckoutRegister checkoutRegister, PaymentCallback callback) {
        this.amountToPay = amount;
        this.checkoutRegister = checkoutRegister;
        this.callback = callback;
        totalSumLabel.setText("Итого к оплате: " + amount + " ₽");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paymentMethodComboBox.getItems().addAll("Наличными", "Банковской картой");
        paymentMethodComboBox.setValue("Наличными");
        
        // Hide card number fields by default
        cardNumberLabel.setVisible(false);
        cardNumberField.setVisible(false);
        
        // Show cash amount fields by default
        cashAmountLabel.setVisible(true);
        cashAmountField.setVisible(true);
        changeLabel.setVisible(true);
        
        paymentMethodComboBox.setOnAction(event -> {
            boolean isCardPayment = "Банковской картой".equals(paymentMethodComboBox.getValue());
            cardNumberLabel.setVisible(isCardPayment);
            cardNumberField.setVisible(isCardPayment);
            cashAmountLabel.setVisible(!isCardPayment);
            cashAmountField.setVisible(!isCardPayment);
            changeLabel.setVisible(!isCardPayment);
            
            if (isCardPayment) {
                cashAmountField.setText("");
                changeLabel.setText("");
            } else {
                cardNumberField.setText("");
            }
        });

        cashAmountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                cashAmountField.setText(oldVal);
            } else if (!newVal.isEmpty()) {
                calculateChange(newVal);
            } else {
                changeLabel.setText("");
            }
        });

        payButton.setOnAction(event -> handlePayment());
        backButton.setOnAction(event -> handleBack());
    }

    private void calculateChange(String amountStr) {
        try {
            BigDecimal paidAmount = new BigDecimal(amountStr);
            BigDecimal difference = paidAmount.subtract(amountToPay);

            if (difference.compareTo(BigDecimal.ZERO) >= 0) {
                changeLabel.setText(String.format("Сдача: %,.2f руб.", difference));
            } else {
                changeLabel.setText("Недостаточно средств!");
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Ошибка ввода");
        }
    }

    public void setDiscountInfo(boolean applied, BigDecimal discount) {
        this.discountApplied = applied;
        this.discount = discount;
    }

    private void handlePayment() {
        String paymentMethod = paymentMethodComboBox.getValue();
        PaymentMethods method;
        
        if ("Наличными".equals(paymentMethod)) {
            method = PaymentMethods.CASH;
        } else if ("Банковской картой".equals(paymentMethod)) {
            method = PaymentMethods.CARD;
            String cardNumber = cardNumberField.getText();
            if (cardNumber == null || cardNumber.trim().isEmpty()) {
                showError("Введите номер карты");
                return;
            }
            if (!isValidCardNumber(cardNumber)) {
                showError("Неверный формат номера карты");
                return;
            }
        } else {
            showError("Выберите способ оплаты");
            return;
        }

        if ("Наличными".equals(paymentMethod)) {
            try {
                BigDecimal paidAmount = new BigDecimal(cashAmountField.getText());
                if (paidAmount.compareTo(amountToPay) < 0) {
                    showError("Внесенная сумма меньше суммы к оплате");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Введите корректную сумму");
                return;
            }
        }

        Receipt receipt = new Receipt(
            CustomerDAO.getCurrentCustomer(),
            checkoutRegister,
            amountToPay,
            method
        );

        if (discountApplied) {
            receipt.setDiscountAmount(amountToPay.multiply(discount));
        }

        try {
            ReceiptDAO.insertNew(receipt);
            showSuccess("Оплата прошла успешно!");
            callback.onPaymentSuccess(receipt);
        } catch (Exception e) {
            showError("Ошибка при сохранении чека: " + e.getMessage());
        }
    }

    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{16}");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleBack() {
        callback.onPaymentCancel();
    }
}
