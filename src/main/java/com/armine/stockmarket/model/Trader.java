package com.armine.stockmarket.model;

import java.util.ArrayList;
import java.util.List;

public class Trader extends User {
    private double balance;
    private Portfolio portfolio;
    private List<Transaction> transactions;

    public Trader(String name, double balance) {
        super(name);
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        this.balance = balance;
        this.portfolio = new Portfolio();
        this.transactions = new ArrayList<>();
    }
    public double getBalance() {
        return balance;
    }
    public Portfolio getPortfolio() {
        return portfolio;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void addMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        balance += amount;
    }
    public void subtractMoney(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Not enough money.");
        }
        balance -= amount;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        transactions.add(transaction);
    }

    @Override
    public String toString() {
        return getName() + " - Balance: $" + balance;
    }
}