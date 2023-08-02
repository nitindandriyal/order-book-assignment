package com.bitvavo.lob.core;

import com.bitvavo.lob.factory.TradeEventFactory;
import com.bitvavo.lob.model.LimitOrderEvent;
import com.bitvavo.lob.model.OrderAction;

import java.util.Locale;

public class OrderMatchingEngine {
    private static final int FURTHEST_DISTANCE_FOR_ACCEPTABLE_PRICE = 1000; // can be a configuration, to weed out the bad prices
    public static final int MAX_PRICE_LIMIT = 999_999;
    private final OrderBookBucket[] bids = new OrderBookBucket[MAX_PRICE_LIMIT];
    private final OrderBookBucket[] asks = new OrderBookBucket[MAX_PRICE_LIMIT];
    private volatile int bidTopOfBook = -1;
    private volatile int askTopOfBook = -1;
    private final TradeEventProcessor tradeEventProcessor;

    private final Locale locale = Locale.US;

    public OrderMatchingEngine(TradeEventProcessor tradeEventProcessor) {
        this.tradeEventProcessor = tradeEventProcessor;
    }

    public void submit(final LimitOrderEvent orderEvent) {
        if (orderEvent.orderAction() == OrderAction.BUY) {
            if (askTopOfBook == -1
                    || asks[askTopOfBook].getLimitOrderEvent().limitPrice() > orderEvent.limitPrice()) {
                addToBidRestingOrders(orderEvent);
            } else {
                int remainingQuantityToFill = sweepAskRestingOrders(orderEvent);
                if (remainingQuantityToFill > 0) {
                    addToBidRestingOrders(orderEvent);
                }
            }
        } else {
            if (bidTopOfBook == -1
                    || bids[bidTopOfBook].getLimitOrderEvent().limitPrice() < orderEvent.limitPrice()) {
                addToAskRestingOrders(orderEvent);
            } else {
                int remainingQuantityToFill = sweepBidRestingOrders(orderEvent);
                if (remainingQuantityToFill > 0) {
                    addToAskRestingOrders(orderEvent);
                }
            }
        }
    }

    private int sweepAskRestingOrders(LimitOrderEvent orderEvent) {
        OrderBookBucket matchingBucket = asks[askTopOfBook];
        int remainingQuantityToFill = orderEvent.quantity();
        while (remainingQuantityToFill > 0
                && askTopOfBook > -1
                && orderEvent.limitPrice() >= matchingBucket.getLimitOrderEvent().limitPrice()) {
            int residual = matchingBucket.getRemainingQuantity() - remainingQuantityToFill;
            if (residual > 0) {
                // can be fully filled
                tradeEventProcessor.submit(TradeEventFactory.createTradeEvent(
                        orderEvent.orderId(),
                        matchingBucket.getLimitOrderEvent().orderId(),
                        orderEvent.orderAction(),
                        matchingBucket.getLimitOrderEvent().limitPrice(),
                        remainingQuantityToFill
                ));
                remainingQuantityToFill = 0;
                matchingBucket.setRemainingQuantity(residual);
            } else {
                // partial fill
                remainingQuantityToFill = remainingQuantityToFill - matchingBucket.getRemainingQuantity();
                tradeEventProcessor.submit(TradeEventFactory.createTradeEvent(
                        orderEvent.orderId(),
                        matchingBucket.getLimitOrderEvent().orderId(),
                        orderEvent.orderAction(),
                        matchingBucket.getLimitOrderEvent().limitPrice(),
                        matchingBucket.getRemainingQuantity()
                ));
                if (matchingBucket.getNext() != null) {
                    matchingBucket = matchingBucket.getNext();
                } else {
                    asks[askTopOfBook] = null;
                    adjustAskTopOfTheBook();
                    if (asks[askTopOfBook] != null) {
                        matchingBucket = asks[askTopOfBook];
                    }
                }
            }
        }
        return remainingQuantityToFill;
    }

    private int sweepBidRestingOrders(LimitOrderEvent orderEvent) {
        OrderBookBucket matchingBucket = bids[bidTopOfBook];
        int remainingQuantityToFill = orderEvent.quantity();
        while (remainingQuantityToFill > 0
                && bidTopOfBook > -1
                && orderEvent.limitPrice() <= matchingBucket.getLimitOrderEvent().limitPrice()) {
            int residual = matchingBucket.getRemainingQuantity() - remainingQuantityToFill;
            if (residual > 0) {
                // can be fully filled
                tradeEventProcessor.submit(TradeEventFactory.createTradeEvent(
                        orderEvent.orderId(),
                        matchingBucket.getLimitOrderEvent().orderId(),
                        orderEvent.orderAction(),
                        matchingBucket.getLimitOrderEvent().limitPrice(),
                        orderEvent.quantity()
                ));
                remainingQuantityToFill = 0;
                matchingBucket.setRemainingQuantity(residual);
            } else {
                // partial fill
                remainingQuantityToFill = remainingQuantityToFill - matchingBucket.getRemainingQuantity();
                tradeEventProcessor.submit(TradeEventFactory.createTradeEvent(
                        orderEvent.orderId(),
                        matchingBucket.getLimitOrderEvent().orderId(),
                        orderEvent.orderAction(),
                        matchingBucket.getLimitOrderEvent().limitPrice(),
                        matchingBucket.getRemainingQuantity()
                ));
                if (matchingBucket.getNext() != null) {
                    matchingBucket = matchingBucket.getNext();
                } else {
                    bids[bidTopOfBook] = null;
                    adjustBidTopOfTheBook();
                    if (bids[bidTopOfBook] != null) {
                        matchingBucket = bids[bidTopOfBook];
                    }
                }
            }
        }
        return remainingQuantityToFill;
    }

    private void adjustBidTopOfTheBook() {
        int furthestDistance = FURTHEST_DISTANCE_FOR_ACCEPTABLE_PRICE; // weeds out bad prices
        while (furthestDistance > 0 && bids[--bidTopOfBook] == null) {
            furthestDistance--;
        }
        if (bidTopOfBook > 0 && bids[bidTopOfBook] != null) {
            bidTopOfBook = -1;
        }
    }

    private void adjustAskTopOfTheBook() {
        int furthestDistance = FURTHEST_DISTANCE_FOR_ACCEPTABLE_PRICE; // weeds out bad prices
        while (furthestDistance > 0 && askTopOfBook > 0 && asks[++askTopOfBook] == null) {
            furthestDistance--;
        }
        if (askTopOfBook > 0 && asks[askTopOfBook] == null) {
            askTopOfBook = -1;
        }
    }

    private void addToBidRestingOrders(LimitOrderEvent orderEvent) {
        if (null == bids[orderEvent.limitPrice()]) {
            bids[orderEvent.limitPrice()] = new OrderBookBucket(orderEvent);
        } else {
            OrderBookBucket tailOrderBookBucket = bids[orderEvent.limitPrice()]; // all on same price levels
            while (tailOrderBookBucket.getNext() != null) {
                tailOrderBookBucket = tailOrderBookBucket.getNext();
            }
            tailOrderBookBucket.setNext(new OrderBookBucket(orderEvent));
        }
        if (bidTopOfBook == -1 || bidTopOfBook < orderEvent.limitPrice()) {
            bidTopOfBook = orderEvent.limitPrice();
        }
    }

    private void addToAskRestingOrders(LimitOrderEvent orderEvent) {
        if (null == asks[orderEvent.limitPrice()] || null == asks[orderEvent.limitPrice()].getLimitOrderEvent()) {
            asks[orderEvent.limitPrice()] = new OrderBookBucket(orderEvent);
        } else {
            OrderBookBucket tailOrderBookBucket = asks[orderEvent.limitPrice()]; // all on same price levels
            while (tailOrderBookBucket.getNext() != null) {
                tailOrderBookBucket = tailOrderBookBucket.getNext();
            }
            tailOrderBookBucket.setNext(new OrderBookBucket(orderEvent));
        }
        if (askTopOfBook == -1 || askTopOfBook > orderEvent.limitPrice()) {
            askTopOfBook = orderEvent.limitPrice();
        }
    }

    public void printOrderBook() {
        for (int i = askTopOfBook, j = bidTopOfBook; i < MAX_PRICE_LIMIT || j > -1; i++, j--) {
            boolean atleastOne = false;
            String leftSide;
            if (j > 0 && bids[j] != null) {
                String quantity = String.format(locale, "%,10d", bids[j].getRemainingQuantity());
                String price = String.format(locale, "%,6d", bids[j].getLimitOrderEvent().limitPrice());
                leftSide = (quantity + " " + price);
                atleastOne = true;
            } else {
                leftSide = String.format("%-12s", "");
            }
            String rightSide;
            if (i > 0 && asks[i] != null) {
                String price = String.format(locale, "%,6d", asks[i].getLimitOrderEvent().limitPrice());
                String quantity = String.format(locale, "%,10d", asks[i].getRemainingQuantity());
                rightSide = (price + " " + quantity);
                atleastOne = true;
            } else {
                rightSide = String.format("%-12s", "");
            }
            if (atleastOne) {
                System.out.println(leftSide + " | " + rightSide);
            }
        }
    }
}
