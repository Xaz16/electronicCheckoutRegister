package edu.istu.achipiga;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;


public class Product {
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private String category;
    public ScanCode scanCode;
    @Getter
    private BigDecimal price;

    public Product(String id, String name, String category, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }


    public int getDiscountedPrice(DiscountCard card) {
//        return price.subtract(price.multiply(card.getDiscountPercent()));
        return 0;
    }

//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public BigInteger getPrice() {
//        return price;
//    }
}