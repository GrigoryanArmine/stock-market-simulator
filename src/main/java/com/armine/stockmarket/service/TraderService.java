package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Trader;

import java.util.ArrayList;
import java.util.List;

public class TraderService {
    private final List<Trader> traders;

    public TraderService() {
        this.traders = new ArrayList<>();
    }

    public void addTrader(Trader trader) {
        if (trader == null) {
            throw new IllegalArgumentException("Trader cannot be null.");
        }
        if (findTrader(trader.getName()) != null) {
            throw new IllegalArgumentException("Trader with this name already exists.");
        }
        traders.add(trader);
    }

    public List<Trader> getAllTraders() {
        return new ArrayList<>(traders);
    }

    public Trader findTrader(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        for (Trader trader : traders) {
            if (trader.getName().equalsIgnoreCase(name)) {
                return trader;
            }
        }
        return null;
    }
}