package com.bitvavo.lob.model;

public record LimitOrderEvent(
        int sequenceNumber,
        String orderId,
        OrderAction orderAction,
        int limitPrice,
        int quantity,
        long receiveTime
) {
}
