package com.armine.stockmarket.model;

import java.util.ArrayList;
import java.util.List;

public class Trader extends User {
    private int id;
    private double balance;
    private Portfolio portfolio;
    private List<Transaction> transactions;

    public Trader(int id, String username, String firstName, String surname, double balance, String passwordHash) {
        super(firstName, surname, username, passwordHash);
        if (id <= 0)
            throw new IllegalArgumentException("Trader ID must be greater than zero.");
        if (!Double.isFinite(balance) || balance < 0)
            throw new IllegalArgumentException("Balance must be a finite, non-negative number.");

        this.id = id;
        this.balance = balance;
        this.portfolio = new Portfolio();
        this.transactions = new ArrayList<>();
    }
    public Trader(Trader other) {
        super(other);

        this.id = other.id;
        this.balance = other.balance;
        this.portfolio = new Portfolio(other.portfolio);
        this.transactions = new ArrayList<>(other.transactions);
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public Portfolio getPortfolio() {
        return new Portfolio(portfolio);
    }

    public int getOwnedShares(Stock stock) {
        return portfolio.getQuantity(stock);
    }

    public void addShares(Stock stock, int quantity) {
        portfolio.addStock(stock, quantity);
    }

    public void removeShares(Stock stock, int quantity) {
        portfolio.removeStock(stock, quantity);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null.");
        transactions.add(transaction);
    }

    public void addMoney(double amount) {
        validateAmount(amount);
        double newBalance = balance + amount;
        if (!Double.isFinite(newBalance)) throw new IllegalArgumentException("Resulting balance is too large.");
        balance = newBalance;
    }

    public void subtractMoney(double amount) {
        validateAmount(amount);
        if (amount > balance) throw new IllegalArgumentException("Not enough money.");
        balance -= amount;
    }

    private void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be a finite, positive number.");
        }
    }

    @Override
    public String toString() {
        return "ID: " + id
                + " | Username: " + getUsername()
                + " | Name: " + getFullName()
                + " | Balance: $" + balance;
    }
}
