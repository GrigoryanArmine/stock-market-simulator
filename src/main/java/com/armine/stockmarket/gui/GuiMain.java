package com.armine.stockmarket.gui;

import com.armine.stockmarket.model.Admin;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.List;

public class GuiMain {
    public static void main(String[] args) {
        try {
            // Initialize market
            MarketPersistenceService marketPersistenceService = new MarketPersistenceService("market.txt");
            MarketService marketService = new MarketService(marketPersistenceService);
            marketPersistenceService.loadMarket(marketService);

            // Initialize trader services
            TraderService traderService = new TraderService();
            TradingService tradingService = new TradingService();
            TraderPersistenceService traderPersistenceService = new TraderPersistenceService("traders");
            TransactionService transactionService = new TransactionService("transactions");

            // Load saved traders and transaction history
            List<Trader> savedTraders = traderPersistenceService.loadAllTraders(marketService);
            for (Trader trader : savedTraders) {
                transactionService.loadTransactions(trader, marketService);
                traderService.addTrader(trader);
            }

            // Initialize demo Admin account
            PasswordService passwordService = new PasswordService();

            char[] demoPassword = "DemoAdmin123!".toCharArray();
            String adminPasswordHash;

            try {
                adminPasswordHash = passwordService.hashPassword(demoPassword);
            } finally {
                Arrays.fill(demoPassword, '\0');
            }
            Admin admin = new Admin("System", "Administrator", "demo_admin", adminPasswordHash);
            AdminService adminService = new AdminService(admin, passwordService);

            // Start Swing GUI on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(traderService, marketService, tradingService, traderPersistenceService, transactionService, adminService);
                mainFrame.setVisible(true);
            });
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Application could not start:\n" + e.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE));
        }
    }
}
