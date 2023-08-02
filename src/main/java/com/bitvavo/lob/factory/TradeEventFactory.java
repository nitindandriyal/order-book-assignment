package com.bitvavo.lob.factory;

import com.bitvavo.lob.model.ExecutedTradeEvent;
import com.bitvavo.lob.model.OrderAction;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.SystemEpochClock;

public class TradeEventFactory {
    private static final EpochClock clock = SystemEpochClock.INSTANCE;

    public static ExecutedTradeEvent createTradeEvent(final String incomingOrderId,
                                                      final String matchedRestingOrderId,
                                                      final OrderAction orderAction,
                                                      final int priceMatched,
                                                      final int quantityMatched) {
        return new ExecutedTradeEvent(
                incomingOrderId,
                matchedRestingOrderId,
                orderAction,
                priceMatched,
                quantityMatched,
                clock.time()
        );
    }
}
