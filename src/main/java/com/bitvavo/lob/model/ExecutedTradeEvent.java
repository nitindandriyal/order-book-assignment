package com.bitvavo.lob.model;

public record ExecutedTradeEvent(
        String incomingOrderId,
        String restingOrderIdMatched,
        OrderAction orderAction,
        int priceMatched,
        int quantityMatched,
        long matchingTime
) {
}
