package com.armine.stockmarket;


import com.armine.stockmarket.exception.InsufficientFundsException;
import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TradingService;

public class Main {

    public static void main(String[] args) {

        Stock apple = new Stock(
                "AAPL",
                "Apple",
                220.50
        );

        Trader trader = new Trader(
                "Armine",
                10000
        );

        TradingService tradingService =
                new TradingService();

        System.out.println(trader);

        try {
            tradingService.buyStock(
                    trader,
                    apple,
                    5
            );

            System.out.println();
            System.out.println("After buying:");
            System.out.println(trader);
            System.out.println(trader.getPortfolio());

            System.out.println();
            System.out.println("Transactions:");

            for (int i = 0;
                 i < trader.getTransactions().size();
                 i++) {

                System.out.println(
                        trader.getTransactions().get(i)
                );
            }

            tradingService.sellStock(
                    trader,
                    apple,
                    2
            );

            System.out.println();
            System.out.println("After selling 2 shares:");
            System.out.println(trader);
            System.out.println(trader.getPortfolio());

        } catch (InsufficientFundsException |
                 InsufficientSharesException e) {

            System.out.println("Transaction failed: "
                    + e.getMessage());
        }
    }
}
