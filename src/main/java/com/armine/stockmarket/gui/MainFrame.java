package com.armine.stockmarket.gui;

import javax.swing.*;

import com.armine.stockmarket.gui.trader.*;
import com.armine.stockmarket.gui.admin.*;
import com.armine.stockmarket.service.*;
import com.armine.stockmarket.model.Trader;
/**
 * Represents the main window of the stock market simulator.
 *
 * MainFrame manages navigation between the graphical
 * interfaces for traders and administrators.
 *
 * It initializes the application window and displays
 * different Swing panels based on user actions.
 *
 * Trading, market management, authentication, and data
 * persistence are handled by the corresponding services.
 */
public class MainFrame extends JFrame {
    /** Service for managing trader accounts. */
    private TraderService traderService;
    /** Service for accessing market stocks. */
    private MarketService marketService;
    /** Service for processing stock trades. */
    private TradingService tradingService;
    /** Service for saving trader accounts. */
    private TraderPersistenceService traderPersistenceService;
    /** Service for saving transaction history. */
    private TransactionService transactionService;
    /** Service for administrator authentication. */
    private AdminService adminService;
    /**
     * Creates the main application window.
     *
     * Initializes the required services, configures the
     * window's title, size, and close operation, and
     * displays the main menu.
     *
     * @param traderService the service managing trader accounts
     * @param marketService the service managing market stocks
     * @param tradingService the service processing trades
     * @param traderPersistenceService the service saving trader data
     * @param transactionService the service managing transaction history
     * @param adminService the service authenticating administrators
     * @throws IllegalArgumentException if any required service is null
     */
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
    /**
     * Displays the specified panel in the main window.
     *
     * Replaces the current content pane and refreshes
     * the window layout and appearance.
     *
     * @param panel the Swing panel to display
     */
    private void showPanel(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
    /**
     * Displays the application's main menu.
     *
     * Provides navigation to the trader menu,
     * administrator login, or application exit.
     */
    private void showMainMenu() {
        MainMenuPanel panel = new MainMenuPanel(
                this::showTraderMenu,
                this::showAdminLogin,
                () -> System.exit(0)
        );
        showPanel(panel);
    }
    /**
     * Displays the trader navigation menu.
     *
     * Allows users to log in, create an account,
     * or return to the main menu.
     */
    private void showTraderMenu() {
        TraderMenuPanel panel = new TraderMenuPanel(
                this::showLogin,
                this::showCreateAccount,
                this::showMainMenu
        );
        showPanel(panel);
    }
    /**
     * Displays the trader login panel.
     *
     * After successful authentication, opens the
     * trader dashboard for the authenticated account.
     */
    private void showLogin() {
        LoginPanel panel = new LoginPanel(
                traderService,
                trader -> showTraderDashboard(trader, false),
                this::showTraderMenu
        );
        showPanel(panel);
    }
    /**
     * Displays the trader registration panel.
     *
     * After successful account creation, opens
     * the dashboard and identifies the session
     * as a newly created account.
     */
    private void showCreateAccount() {
        CreateAccountPanel panel = new CreateAccountPanel(
                traderService,
                traderPersistenceService,
                trader -> showTraderDashboard(trader, true),
                this::showTraderMenu
        );
        showPanel(panel);
    }
    /**
     * Displays the dashboard for an authenticated trader.
     *
     * Provides navigation to the market, portfolio,
     * transaction history, and trader menu.
     *
     * @param trader the authenticated trader
     * @param newAccount whether the account was newly created
     */
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
    /**
     * Displays the market panel for a trader.
     *
     * Provides access to available stocks and
     * stock purchasing operations.
     *
     * @param trader the trader viewing the market
     */
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
    /**
     * Displays the trader's portfolio panel.
     *
     * Provides access to current holdings and
     * stock selling operations.
     *
     * @param trader the trader whose portfolio is displayed
     */
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
    /**
     * Displays the trader's transaction history.
     *
     * Provides navigation back to the trader dashboard.
     *
     * @param trader the trader whose transactions are displayed
     */
    private void showTransactions(Trader trader) {
        TransactionsPanel panel = new TransactionsPanel(
                trader,
                () -> showTraderDashboard(trader, false)
        );
        showPanel(panel);
    }
    /**
     * Displays the administrator login panel.
     *
     * After successful authentication, opens
     * the administrator dashboard.
     */
    private void showAdminLogin() {
        AdminLoginPanel panel = new AdminLoginPanel(
                adminService,
                admin -> showAdminDashboard(),
                this::showMainMenu
        );
        showPanel(panel);
    }
    /**
     * Displays the administrator dashboard.
     *
     * Provides navigation to stock management,
     * registered trader information, and the main menu.
     */
    private void showAdminDashboard() {
        AdminDashboardPanel panel = new AdminDashboardPanel(
                this::showManageStocks,
                this::showManageTraders,
                this::showMainMenu
        );
        showPanel(panel);
    }
    /**
     * Displays the stock management panel.
     *
     * Allows administrators to manage available
     * stocks and their market prices.
     */
    private void showManageStocks() {
        ManageStocksPanel panel = new ManageStocksPanel(
                marketService,
                traderService,
                this::showAdminDashboard
        );
        showPanel(panel);
    }
    /**
     * Displays the registered trader management panel.
     *
     * Provides administrators with access to
     * registered trader information.
     */
    private void showManageTraders() {
        ManageTradersPanel panel = new ManageTradersPanel(
                traderService,
                this::showAdminDashboard
        );
        showPanel(panel);
    }
}
