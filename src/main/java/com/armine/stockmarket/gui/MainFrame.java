package com.armine.stockmarket.gui;
import javax.swing.*;
import java.awt.Component;
import java.awt.Dimension;


import com.armine.stockmarket.gui.trader.*;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TransactionService;


public class MainFrame extends JFrame {
    private TraderService traderService;
    private MarketService marketService;
    private TradingService tradingService;
    private TraderPersistenceService traderPersistenceService;
    private TransactionService transactionService;

    public MainFrame(TraderService traderService, MarketService marketService, TradingService tradingService, TraderPersistenceService traderPersistenceService, TransactionService transactionService) {
        this.traderService = traderService;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.traderPersistenceService = traderPersistenceService;
        this.transactionService = transactionService;

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
                () -> JOptionPane.showMessageDialog(this, "Admin functionality is coming soon."),
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


}
