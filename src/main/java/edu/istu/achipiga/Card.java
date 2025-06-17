package edu.istu.achipiga;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public abstract class Card {
    @Getter
    @Setter
    private String id;

    @Getter
    private String number;

    public abstract String getCardType();
}
