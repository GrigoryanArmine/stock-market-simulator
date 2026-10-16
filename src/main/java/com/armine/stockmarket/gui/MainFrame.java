package com.armine.stockmarket.gui;

import javax.swing.*;

import com.armine.stockmarket.gui.trader.*;
import com.armine.stockmarket.gui.admin.*;
import com.armine.stockmarket.service.*;
import com.armine.stockmarket.model.Trader;

public class MainFrame extends JFrame {
    private TraderService traderService;
    private MarketService marketService;
    private TradingService tradingService;
    private TraderPersistenceService traderPersistenceService;
    private TransactionService transactionService;
    private AdminService adminService;

    public MainFrame(TraderService traderService, MarketService marketService, TradingService tradingService, TraderPersistenceService traderPersistenceService, TransactionService transactionService, AdminService adminService) {
        if (traderService == null || marketService == null || tradingService == null || traderPersistenceService == null || transactionService == null || adminService == null)
            throw new IllegalArgumentException("Services cannot be null.");

        this.traderService = traderService;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.traderPersistenceService = traderPersistenceService;
        this.transactionService = transactionService;
        this.adminService = adminService;

        setTitle("Stock Market Simulator");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showMainMenu();
    }

    // Shared navigation method
    private void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void showMainMenu() {
        MainMenuPanel panel = new MainMenuPanel(
                this::showTraderMenu,
                this::showAdminLogin,
                () -> System.exit(0)
        );
        showPanel(panel);
    }

    private void showTraderMenu() {
        TraderMenuPanel panel = new TraderMenuPanel(
                this::showLogin,
                this::showCreateAccount,
                this::showMainMenu
        );
        showPanel(panel);
    }

    private void showLogin() {
        LoginPanel panel = new LoginPanel(
                traderService,
                trader -> showTraderDashboard(trader, false),
                this::showTraderMenu
        );
        showPanel(panel);
    }

    private void showCreateAccount() {
        CreateAccountPanel panel = new CreateAccountPanel(
                traderService,
                traderPersistenceService,
                trader -> showTraderDashboard(trader, true),
                this::showTraderMenu
        );
        showPanel(panel);
    }

    private void showTraderDashboard(Trader trader, boolean newAccount) {
        TraderDashboardPanel panel = new TraderDashboardPanel(
                trader,
                newAccount,
                () -> showMarket(trader),
                () -> showPortfolio(trader),
                () -> showTransactions(trader),
                this::showTraderMenu
        );
        showPanel(panel);
    }

    private void showMarket(Trader trader) {
        MarketPanel panel = new MarketPanel(
                this,
                trader,
                marketService,
                tradingService,
                traderPersistenceService,
                transactionService,
                () -> showTraderDashboard(trader, false),
                () -> showMarket(trader)
        );
        showPanel(panel);
    }

    private void showPortfolio(Trader trader) {
        PortfolioPanel panel = new PortfolioPanel(
                this,
                trader,
                tradingService,
                traderPersistenceService,
                transactionService,
                () -> showTraderDashboard(trader, false),
                () -> showPortfolio(trader)
        );
        showPanel(panel);
    }

    private void showTransactions(Trader trader) {
        TransactionsPanel panel = new TransactionsPanel(
                trader,
                () -> showTraderDashboard(trader, false)
        );
        showPanel(panel);
    }

    private void showAdminLogin() {
        AdminLoginPanel panel = new AdminLoginPanel(
                adminService,
                admin -> showAdminDashboard(),
                this::showMainMenu
        );
        showPanel(panel);
    }

    private void showAdminDashboard() {
        AdminDashboardPanel panel = new AdminDashboardPanel(
                this::showManageStocks,
                this::showManageTraders,
                this::showMainMenu
        );
        showPanel(panel);
    }

    private void showManageStocks() {
        ManageStocksPanel panel = new ManageStocksPanel(
                marketService,
                traderService,
                this::showAdminDashboard
        );
        showPanel(panel);
    }

    private void showManageTraders() {
        ManageTradersPanel panel = new ManageTradersPanel(
                traderService,
                this::showAdminDashboard
        );
        showPanel(panel);
    }
}
