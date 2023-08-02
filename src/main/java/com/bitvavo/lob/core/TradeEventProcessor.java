package com.bitvavo.lob.core;

import com.bitvavo.lob.model.ExecutedTradeEvent;

import java.util.ArrayList;
import java.util.List;

public class TradeEventProcessor {
    private final List<ExecutedTradeEvent> tradeEvents;

    public TradeEventProcessor() {
        tradeEvents = new ArrayList<>();
    }

    public void submit(ExecutedTradeEvent executedTradeEvent) {
        tradeEvents.add(executedTradeEvent);
    }

    public void printTradeQueue() {
        for (ExecutedTradeEvent tradeEvent : tradeEvents) {
            System.out.println("trade "
                    + tradeEvent.incomingOrderId() + ","
                    + tradeEvent.restingOrderIdMatched() + ","
                    + tradeEvent.priceMatched() + ","
                    + tradeEvent.quantityMatched()
            );
        }
    }
}
