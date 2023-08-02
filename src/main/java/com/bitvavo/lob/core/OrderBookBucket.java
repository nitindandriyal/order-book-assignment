package com.bitvavo.lob.core;

import com.bitvavo.lob.model.LimitOrderEvent;

public class OrderBookBucket {
    private LimitOrderEvent limitOrderEvent;
    private OrderBookBucket next = null;
    private int remainingQuantity;

    public OrderBookBucket(LimitOrderEvent limitOrderEvent) {
        this.limitOrderEvent = limitOrderEvent;
        this.remainingQuantity = limitOrderEvent.quantity();
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
