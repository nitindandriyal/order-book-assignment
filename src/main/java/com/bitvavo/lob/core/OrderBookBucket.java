package com.bitvavo.lob.core;

import com.bitvavo.lob.model.LimitOrderEvent;

public class OrderBookBucket {
    private final LimitOrderEvent limitOrderEvent;
    private OrderBookBucket next = null;
    private int remainingQuantity;

    public OrderBookBucket(LimitOrderEvent limitOrderEvent, int remainingQuantity) {
        this.limitOrderEvent = limitOrderEvent;
        this.remainingQuantity = remainingQuantity;
    }

    public LimitOrderEvent getLimitOrderEvent() {
        return limitOrderEvent;
    }

    public OrderBookBucket getNext() {
        return next;
    }

    public void setNext(OrderBookBucket next) {
        this.next = next;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }
}
