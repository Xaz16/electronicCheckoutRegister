package edu.istu.achipiga;

public enum PaymentMethods {
    CASH("Наличными"),
    CARD("Банковской картой");

    private final String label;

    PaymentMethods(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
