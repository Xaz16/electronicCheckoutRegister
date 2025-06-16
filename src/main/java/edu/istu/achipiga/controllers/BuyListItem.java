package edu.istu.achipiga.controllers;

import edu.istu.achipiga.CheckoutRegister;
import edu.istu.achipiga.Product;
import edu.istu.achipiga.dao.CheckoutRegisterDAO;
import javafx.beans.property.*;
import lombok.Getter;

import java.math.BigDecimal;

public class BuyListItem {
    private edu.istu.achipiga.BuyListItem item;

    private final SimpleStringProperty productId;
    private final SimpleStringProperty productName;
    private final ObjectProperty<BigDecimal> price;
    private final IntegerProperty quantity;
    private final ObjectProperty<BigDecimal> sum;

    @Getter
    private final Product product;

    public BuyListItem(Product product, int quantity) {
        this.product = product;
        this.productId = new SimpleStringProperty(product.getId());
        this.productName = new SimpleStringProperty(product.getName());
        this.price = new SimpleObjectProperty<>(product.getPrice());
        this.quantity = new SimpleIntegerProperty(quantity);
        this.sum = new SimpleObjectProperty<>(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        // Автоматический пересчет суммы при изменении количества
        this.quantity.addListener((obs, oldVal, newVal) -> {
            this.sum.set(this.price.get().multiply(BigDecimal.valueOf(newVal.intValue())));
        });
    }

    public StringProperty productNameProperty() { return productName; }
    public ObjectProperty<BigDecimal> priceProperty() { return price; }
    public IntegerProperty quantityProperty() { return quantity; }
    public ObjectProperty<BigDecimal> sumProperty() { return sum; }

    public String getProductId() { return productId.get(); }
    public String getProductName() { return productName.get(); }
    public BigDecimal getPrice() { return price.get(); }
    public int getQuantity() { return quantity.get(); }
    public BigDecimal getSum() { return sum.get(); }
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
}
