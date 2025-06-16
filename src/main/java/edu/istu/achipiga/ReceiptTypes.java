package edu.istu.achipiga;

public enum ReceiptTypes {
    PAYMENT("Оплата"),
    REFUND("Возврат");

    private final String label;

    ReceiptTypes(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
} 