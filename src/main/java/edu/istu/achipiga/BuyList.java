package edu.istu.achipiga;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Aleksey Chipiga
 */
public class BuyList {
    /**
     * 
     */
    @Getter
    private int id = new Random().nextInt(1_000_001);

    /**
     * 
     */
    @Getter
    private List<BuyListItem> items = new ArrayList<>();

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return
     */
    public BigDecimal getTotalSum() {
        return items.stream()
                .map(buyListItem -> buyListItem.getTotalSum())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @param item 
     * @return
     */
    public void addItem(BuyListItem item) {
        items.add(item);
    }

    /**
     * @param item 
     * @return
     */
    public void removeItem(BuyListItem item) {
        items.remove(item);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public Optional<BuyListItem> getByProduct(String productId) {
        return items.stream().filter(item -> Objects.equals(item.getProduct().getId(), productId)).findFirst();
    }
}