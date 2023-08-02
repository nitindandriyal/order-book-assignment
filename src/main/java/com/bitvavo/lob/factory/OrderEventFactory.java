package com.bitvavo.lob.factory;

import com.bitvavo.lob.model.LimitOrderEvent;
import com.bitvavo.lob.model.OrderAction;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.SystemEpochClock;

public class OrderEventFactory {

    private static final EpochClock clock = SystemEpochClock.INSTANCE;

    public static LimitOrderEvent createLimitOrderEvent(final String[] orderTokens) {
        return new LimitOrderEvent(
                OrderSequencer.nextSequence(),
                orderTokens[0],
                OrderAction.of(orderTokens[1].charAt(0)),
                Integer.parseInt(orderTokens[2]),
                Integer.parseInt(orderTokens[3]),
                clock.time()
        );
    }
}
