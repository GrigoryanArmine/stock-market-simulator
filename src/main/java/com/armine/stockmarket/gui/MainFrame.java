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

    public MainFrame(
            TraderService traderService,
            MarketService marketService,
            TradingService tradingService,
            TraderPersistenceService traderPersistenceService,
            TransactionService transactionService,
            AdminService adminService) {
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

    private void showMainMenu() {
        getContentPane().removeAll();

        MainMenuPanel mainMenuPanel = new MainMenuPanel(
                () -> showTraderMenu(),
                () -> showAdminLogin(),
                () -> System.exit(0)
        );

        add(mainMenuPanel);

        revalidate();
        repaint();
    }

    private void showTraderMenu() {
        getContentPane().removeAll();

        TraderMenuPanel traderMenuPanel = new TraderMenuPanel(
                () -> showLogin(),
                () -> showCreateAccount(),
                () -> showMainMenu()
        );

        add(traderMenuPanel);

        revalidate();
        repaint();
    }


    private void showLogin() {
        getContentPane().removeAll();

        LoginPanel loginPanel = new LoginPanel(traderService, trader -> showTraderDashboard(trader, false), () -> showTraderMenu());

        add(loginPanel);

        revalidate();
        repaint();
    }



    private void showCreateAccount() {
        getContentPane().removeAll();

        CreateAccountPanel createAccountPanel =
                new CreateAccountPanel(traderService, traderPersistenceService,
                        trader -> showTraderDashboard(trader, true),
                        () -> showTraderMenu()
                );

        add(createAccountPanel);

        revalidate();
        repaint();
    }

    private void showTraderDashboard(Trader trader, boolean newAccount) {
        getContentPane().removeAll();

        TraderDashboardPanel dashboardPanel = new TraderDashboardPanel(trader, newAccount,
                        () -> showMarket(trader),
                        () -> showPortfolio(trader),
                        () -> showTransactions(trader),
                        () -> showTraderMenu()
        );

        add(dashboardPanel);

        revalidate();
        repaint();
    }

    private void showMarket(Trader trader) {
        getContentPane().removeAll();

        MarketPanel marketPanel = new MarketPanel(
                this,
                trader,
                marketService,
                tradingService,
                traderPersistenceService,
                transactionService,
                () -> showTraderDashboard(trader, false),
                () -> showMarket(trader)
        );

        add(marketPanel);

        revalidate();
        repaint();
    }



    private void showPortfolio(Trader trader) {
        getContentPane().removeAll();

        PortfolioPanel portfolioPanel = new PortfolioPanel(
                this,
                trader,
                tradingService,
                traderPersistenceService,
                transactionService,
                () -> showTraderDashboard(trader, false),
                () -> showPortfolio(trader)
        );

        add(portfolioPanel);
        revalidate();
        repaint();
    }

    private void showTransactions(Trader trader) {
        getContentPane().removeAll();

        TransactionsPanel transactionsPanel = new TransactionsPanel(trader, () -> showTraderDashboard(trader, false));

        add(transactionsPanel);
        revalidate();
        repaint();
    }


    private void showAdminLogin() {
        getContentPane().removeAll();

        AdminLoginPanel loginPanel = new AdminLoginPanel(adminService, admin -> showAdminDashboard(), () -> showMainMenu());
        add(loginPanel);

        revalidate();
        repaint();
    }



    private void showAdminDashboard() {
        getContentPane().removeAll();

        AdminDashboardPanel dashboardPanel = new AdminDashboardPanel(
                () -> showManageStocks(),
                () -> showManageTraders(),
                () -> showMainMenu()
        );
        add(dashboardPanel);
        revalidate();
        repaint();
    }

    private void showManageStocks() {
        getContentPane().removeAll();
        ManageStocksPanel manageStocksPanel = new ManageStocksPanel(marketService, traderService, ()->showAdminDashboard());
        add(manageStocksPanel);
        revalidate();
        repaint();
    }

    private void showManageTraders() {
        getContentPane().removeAll();

        ManageTradersPanel manageTradersPanel = new ManageTradersPanel(
                traderService,
                ()-> showAdminDashboard()
        );

        add(manageTradersPanel);

        revalidate();
        repaint();
    }
}
