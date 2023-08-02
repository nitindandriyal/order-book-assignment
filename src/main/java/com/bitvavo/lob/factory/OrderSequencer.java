package com.bitvavo.lob.factory;

import java.util.concurrent.atomic.AtomicInteger;

class OrderSequencer {
    private static final AtomicInteger sequence = new AtomicInteger(1);

    static int nextSequence() {
        return sequence.getAndIncrement();
    }
}
