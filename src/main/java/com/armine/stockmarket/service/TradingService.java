package com.armine.stockmarket.service;

import com.armine.stockmarket.exception.InsufficientFundsException;
import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Transaction;
import com.armine.stockmarket.model.TransactionType;
import com.armine.stockmarket.model.Trader;

public class TradingService {

    public void validateBuy(Trader trader, Stock stock, int quantity) throws InsufficientFundsException {
        validateInputs(trader, stock, quantity);
        if (stock.getCurrentPrice() * quantity > trader.getBalance()) {
            throw new InsufficientFundsException("Trader does not have enough money.");
        }
    }

    public void validateSell(Trader trader, Stock stock, int quantity) throws InsufficientSharesException {
        validateInputs(trader, stock, quantity);
        if (quantity > trader.getPortfolio().getQuantity(stock)) {
            throw new InsufficientSharesException("Trader does not have enough shares.");
        }
    }

    private void validateInputs(Trader trader, Stock stock, int quantity) {
        if (trader == null) {
            throw new IllegalArgumentException("Trader cannot be null.");
        }
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    public void buyStock(Trader trader, Stock stock, int quantity) throws InsufficientFundsException {
        validateBuy(trader, stock, quantity);

        double totalPrice = stock.getCurrentPrice() * quantity;
        trader.subtractMoney(totalPrice);
        trader.getPortfolio().addStock(stock, quantity);

        Transaction transaction = new Transaction(stock, quantity, stock.getCurrentPrice(), TransactionType.BUY);
        trader.addTransaction(transaction);
    }

    public void sellStock(Trader trader, Stock stock, int quantity) throws InsufficientSharesException {
        validateSell(trader, stock, quantity);

        double totalPrice = stock.getCurrentPrice() * quantity;
        trader.getPortfolio().removeStock(stock, quantity);
        trader.addMoney(totalPrice);

        Transaction transaction = new Transaction(stock, quantity, stock.getCurrentPrice(), TransactionType.SELL);
        trader.addTransaction(transaction);
    }
}
