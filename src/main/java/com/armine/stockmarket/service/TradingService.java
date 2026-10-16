package com.armine.stockmarket.service;

import com.armine.stockmarket.exception.*;
import com.armine.stockmarket.model.*;

public class TradingService {
    public void validateBuy(Trader trader, Stock stock, int quantity) throws InsufficientFundsException {
        validateInputs(trader, stock, quantity);
        double totalPrice = calculateTotalPrice(stock.getCurrentPrice(), quantity);

        if (totalPrice > trader.getBalance())
            throw new InsufficientFundsException("Trader does not have enough money.");

        int ownedShares = trader.getOwnedShares(stock);
        if (quantity > Integer.MAX_VALUE - ownedShares)
            throw new IllegalArgumentException("Maximum share quantity exceeded.");
    }

    public void validateSell(Trader trader, Stock stock, int quantity) throws InsufficientSharesException {
        validateInputs(trader, stock, quantity);
        if (quantity > trader.getOwnedShares(stock))
            throw new InsufficientSharesException("Trader does not have enough shares.");

        double totalPrice = calculateTotalPrice(stock.getCurrentPrice(), quantity);

        if (!Double.isFinite(trader.getBalance() + totalPrice))
            throw new IllegalArgumentException("Resulting balance is too large.");
    }

    public void buyStock(Trader trader, Stock stock, int quantity) throws InsufficientFundsException {
        validateBuy(trader, stock, quantity);

        double pricePerShare = stock.getCurrentPrice();
        double totalPrice = calculateTotalPrice(pricePerShare, quantity);

        Transaction transaction = new Transaction(stock, quantity, pricePerShare, TransactionType.BUY);

        trader.subtractMoney(totalPrice);
        trader.addShares(stock, quantity);
        trader.addTransaction(transaction);
    }

    public void sellStock(Trader trader, Stock stock, int quantity) throws InsufficientSharesException {
        validateSell(trader, stock, quantity);

        double pricePerShare = stock.getCurrentPrice();
        double totalPrice = calculateTotalPrice(pricePerShare, quantity);

        Transaction transaction = new Transaction(stock, quantity, pricePerShare, TransactionType.SELL);

        trader.removeShares(stock, quantity);
        trader.addMoney(totalPrice);
        trader.addTransaction(transaction);
    }

    private void validateInputs(Trader trader, Stock stock, int quantity) {
        if (trader == null) throw new IllegalArgumentException("Trader cannot be null.");
        if (stock == null) throw new IllegalArgumentException("Stock cannot be null.");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
    }

    private double calculateTotalPrice(double pricePerShare, int quantity) {
        double totalPrice = pricePerShare * quantity;
        if (!Double.isFinite(totalPrice) || totalPrice <= 0) throw new IllegalArgumentException("Invalid transaction amount.");
        return totalPrice;
    }
}
