package com.armine.stockmarket.model;

import com.armine.stockmarket.service.MarketService;

public class Admin extends User {
    public Admin(String firstName, String surname, String username, String passwordHash) {
        super(firstName, surname, username, passwordHash);
    }

    public void addStockToMarket(Stock stock, MarketService marketService) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }
        marketService.addStock(stock);
    }

    @Override
    public String toString() {
        return "Admin: " + getFullName();
    }
}
