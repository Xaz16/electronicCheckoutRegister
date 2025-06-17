package edu.istu.achipiga.controllers;

import edu.istu.achipiga.*;
import edu.istu.achipiga.dao.CustomerDAO;
import edu.istu.achipiga.dao.ReceiptDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    private Label changeLabelValue;
    @FXML
    private Label changeLabel;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private Button payButton;
    @FXML
    private Button backButton;

    private CheckoutRegister checkoutRegister;
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
        paymentMethodComboBox.getItems().addAll(PaymentMethods.CASH.getLabel(), PaymentMethods.CARD.getLabel());
        paymentMethodComboBox.setValue(PaymentMethods.CASH.getLabel());
        
        
        cardNumberLabel.setVisible(false);
        cardNumberField.setVisible(false);
        
        cashAmountLabel.setVisible(true);
        cashAmountField.setVisible(true);
        changeLabelValue.setVisible(true);
        changeLabel.setVisible(true);

        payButton.setDisable(true);
        
        paymentMethodComboBox.setOnAction(event -> {
            boolean isCardPayment = PaymentMethods.CARD.getLabel().equals(paymentMethodComboBox.getValue());
            cardNumberLabel.setVisible(isCardPayment);
            cardNumberField.setVisible(isCardPayment);

            cashAmountLabel.setVisible(!isCardPayment);
            cashAmountField.setVisible(!isCardPayment);

            changeLabel.setVisible(!isCardPayment);
            changeLabelValue.setVisible(!isCardPayment);
            if (isCardPayment) {
                cashAmountField.setText("");
                changeLabelValue.setText("");

                BankCard bankCard = CustomerDAO.getCurrentCustomer().getBankCard();
                if (bankCard == null) {
                    showError("У клиента нет банковской карты");
                    return;
                }
                if (isValidCardNumber(bankCard.getNumber())) {
                    cardNumberField.setText("**** **** **** " + bankCard.getNumber().substring(12));
                    cardNumberField.setEditable(false);
                }
                payButton.setDisable(false);
                cardNumberField.setVisible(true);

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
                changeLabelValue.setText("");
            }
        });

        payButton.setOnAction(event -> handlePayment());
        backButton.setOnAction(event -> handleBack());
    }

    private void calculateChange(String amountStr) {
        boolean isValid = false;
        try {
            BigDecimal paidAmount = new BigDecimal(amountStr);
            BigDecimal difference = paidAmount.subtract(amountToPay);

            if (difference.compareTo(BigDecimal.ZERO) >= 0) {
                changeLabelValue.setText(String.format("Сдача: %,.2f руб.", difference));
                isValid = true;
            } else {
                changeLabelValue.setText("Недостаточно средств!");
                
            }
        } catch (NumberFormatException e) {
            changeLabelValue.setText("Ошибка ввода");
        }

        payButton.setDisable(!isValid);
    }

    public void setDiscountInfo(boolean applied, BigDecimal discount) {
        this.discountApplied = applied;
        this.discount = discount;
    }

    private void handlePayment() {
        String paymentMethod = paymentMethodComboBox.getValue();
        PaymentMethods method;
        BigDecimal paidAmount;
        if (PaymentMethods.CASH.getLabel().equals(paymentMethod)) {
            method = PaymentMethods.CASH;
            paidAmount = new BigDecimal(cashAmountField.getText());

        } else if (PaymentMethods.CARD.getLabel().equals(paymentMethod)) {
            method = PaymentMethods.CARD;
            paidAmount = amountToPay;
        } else {
            showError("Выберите способ оплаты");
            return;
        }

        if (method == PaymentMethods.CASH) {
            try {
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
            paidAmount,
            method
        );
        
        
        if(method == PaymentMethods.CARD) {
            BankCard bankCard = CustomerDAO.getCurrentCustomer().getBankCard();
            receipt.setBankCard(bankCard);
        } else {
            receipt.setBankCard(null);
        }
        
        receipt.setTotalAmount(amountToPay);

        if (discountApplied) {
            receipt.setDiscountAmount(amountToPay.multiply(discount));
        }

        try {
            callback.onPaymentSuccess(receipt);
        } catch (Exception e) {
            showError("Ошибка при сохранении чека: " + e.getMessage());
            throw e;
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

    private void handleBack() {
        callback.onPaymentCancel();
    }
}
