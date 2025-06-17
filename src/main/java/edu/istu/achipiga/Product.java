package edu.istu.achipiga;

import lombok.Getter;

import java.math.BigDecimal;

public class Product {
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private String category;
    @Getter
    private BigDecimal price;

    public Product(String id, String name, String category, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public int getDiscountedPrice(DiscountCard card) {
        return 0;
    }
}