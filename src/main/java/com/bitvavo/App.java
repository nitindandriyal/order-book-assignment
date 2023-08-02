package com.bitvavo;

import com.bitvavo.lob.core.OrderMatchingEngine;
import com.bitvavo.lob.core.TradeEventProcessor;
import com.bitvavo.lob.factory.OrderEventFactory;
import com.bitvavo.lob.model.LimitOrderEvent;

import java.util.Scanner;

public class App {

    private static final String delimiter = ",";

    public static void main(String[] args) {
        TradeEventProcessor tradeEventProcessor = new TradeEventProcessor();
        OrderMatchingEngine orderMatchingEngine = new OrderMatchingEngine(tradeEventProcessor);
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(delimiter);
        while (scanner.hasNextLine()) {
            String inputLine = scanner.nextLine();
            if (inputLine.trim().equals("")) {
                break;
            }
            String[] orderTokens = inputLine.split(delimiter);
            LimitOrderEvent limitOrderEvent = OrderEventFactory.createLimitOrderEvent(orderTokens);
            orderMatchingEngine.submit(limitOrderEvent);
        }
        tradeEventProcessor.printTradeQueue();
        orderMatchingEngine.printOrderBook();
    }
}
