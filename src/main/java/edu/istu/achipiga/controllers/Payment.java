package edu.istu.achipiga.controllers;

import edu.istu.achipiga.CheckoutRegister;
import edu.istu.achipiga.Customer;
import edu.istu.achipiga.PaymentMethods;
import edu.istu.achipiga.Receipt;
import edu.istu.achipiga.dao.CheckoutRegisterDAO;
import edu.istu.achipiga.dao.CustomerDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Payment {
    @FXML private Label amountLabel;
    @FXML private Label changeLabel;
    @FXML private Label statusLabel;
    @FXML private TextField cashAmountField;
    @FXML private TextField cardNumberField;
    @FXML private RadioButton cashRadioButton;
    @FXML private RadioButton cardRadioButton;
    @FXML private ToggleGroup paymentMethodGroup;

    private BigDecimal amountToPay = BigDecimal.ZERO;
    private PaymentCallback callback;
    private boolean discountApplied = false;
    private BigDecimal discount = BigDecimal.ZERO;

    Customer customer = CustomerDAO.getCurrentCustomer();
    CheckoutRegister checkoutRegister = CheckoutRegisterDAO.getCurrentCheckoutRegister();

    public interface PaymentCallback {
        void onPaymentSuccess(Receipt receipt);
        void onPaymentCancel();
    }

    @FXML
    public void initialize() {
        cashRadioButton.setToggleGroup(paymentMethodGroup);
        cardRadioButton.setToggleGroup(paymentMethodGroup);
        cashRadioButton.setSelected(true);

        // Initially show cash-related fields and hide card-related fields
        cashAmountField.setVisible(true);
        cardNumberField.setVisible(false);
        changeLabel.setVisible(true);

        paymentMethodGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String method = newVal.getUserData().toString();
                boolean isCash = "CASH".equals(method);
                
                cashAmountField.setVisible(isCash);
                cardNumberField.setVisible(!isCash);
                changeLabel.setVisible(isCash);

                if (isCash) {
                    cashAmountField.setText("");
                    changeLabel.setText("");
                } else {
                    cardNumberField.setText("");
                }
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
    }

    public void setup(BigDecimal amount, PaymentCallback callback) {
        this.amountToPay = amount;
        this.callback = callback;
        amountLabel.setText(String.format("%,.2f руб.", amount));
    }

    public void setDiscountInfo(boolean applied, BigDecimal discount) {
        this.discountApplied = applied;
        this.discount = discount;
    }

    private void calculateChange(String amountStr) {
        if (amountToPay == null) {
            changeLabel.setText("Ошибка: сумма к оплате не установлена");
            return;
        }

        try {
            BigDecimal paidAmount = new BigDecimal(amountStr);
            BigDecimal difference = paidAmount.subtract(amountToPay);

            if (difference.compareTo(BigDecimal.ZERO) >= 0) {
                changeLabel.setText(String.format("%,.2f руб.", difference));
                statusLabel.setText("");
            } else {
                changeLabel.setText("Недостаточно средств!");
                statusLabel.setText("Внесите еще " +
                        String.format("%,.2f руб.", difference.abs()));
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Ошибка ввода");
        }
    }

    @FXML
    private void processPayment() {
        PaymentMethods method = PaymentMethods.valueOf(paymentMethodGroup.getSelectedToggle().getUserData().toString());

        switch (method) {
            case CASH:
                processCashPayment();
                break;
            case CARD:
                processCardPayment();
                break;
        }
    }

    private void processCashPayment() {
        try {
            BigDecimal paidAmount = new BigDecimal(cashAmountField.getText());
            if (paidAmount.compareTo(amountToPay) >= 0) {
                Receipt receipt = new Receipt(customer, checkoutRegister, paidAmount, PaymentMethods.CASH);
                if (discountApplied) {
                    receipt.setDiscountAmount(checkoutRegister.getBuyList().getTotalSum().multiply(discount));
                }
                callback.onPaymentSuccess(receipt);
            } else {
                statusLabel.setText("Внесенная сумма меньше суммы к оплате!");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Введите корректную сумму!");
        }
    }

    private void processCardPayment() {
        String cardNumber = cardNumberField.getText().trim();
        if (cardNumber.matches("\\d{4} \\d{4} \\d{4} \\d{4}")) {
            Receipt receipt = new Receipt(customer, checkoutRegister, amountToPay, PaymentMethods.CARD);
            if (discountApplied) {
                receipt.setDiscountAmount(checkoutRegister.getBuyList().getTotalSum().multiply(discount));
            }
            callback.onPaymentSuccess(receipt);
        } else {
            statusLabel.setText("Введите корректный номер карты!");
        }
    }

    @FXML
    private void goBack() {
        callback.onPaymentCancel();
    }
}
