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
        if (id <= 0) {
            throw new IllegalArgumentException("Trader ID must be greater than zero.");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        this.id = id;
        this.balance = balance;
        this.portfolio = new Portfolio();
        this.transactions = new ArrayList<>();
    }

    public int getId() {
        return id;
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
        return "ID: " + id
                + " | Username: " + getUsername()
                + " | Name: " + getFullName()
                + " | Balance: $" + balance;
    }
}