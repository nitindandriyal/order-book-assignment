package com.bitvavo.lob.model;

public enum OrderAction {
    BUY('B'),
    SELL('S');

    private final char charCode;

    public char getCharCode() {
        return charCode;
    }

    OrderAction(char charCode) {
        this.charCode = charCode;
    }

    public static OrderAction of(char code) {
        return switch (code) {
            case 'B' -> BUY;
            case 'S' -> SELL;
            default -> throw new IllegalArgumentException("Unknown Side:" + code);
        };
    }

    public OrderAction opposite() {
        return this == BUY ? SELL : BUY;
    }
}
