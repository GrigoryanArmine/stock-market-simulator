package com.armine.stockmarket.gui;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;


import com.armine.stockmarket.model.Stock;
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
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showMainMenu();
    }
    private void showMainMenu() {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("STOCK MARKET SIMULATOR");

        JButton traderButton = new JButton("Trader");
        JButton adminButton = new JButton("Admin");
        JButton exitButton = new JButton("Exit");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        traderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        traderButton.addActionListener(e -> showTraderMenu());
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(Box.createVerticalStrut(60));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(40));
        panel.add(traderButton);

        panel.add(Box.createVerticalStrut(15));
        panel.add(adminButton);

        panel.add(Box.createVerticalStrut(15));
        panel.add(exitButton);
        add(panel);

        revalidate();
        repaint();

    }

    private void showTraderMenu() {

        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> showLogin());
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(e -> showCreateAccount());
        JButton backButton = new JButton("Back");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(60));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(40));
        panel.add(loginButton);

        panel.add(Box.createVerticalStrut(15));
        panel.add(createAccountButton);

        panel.add(Box.createVerticalStrut(15));
        panel.add(backButton);

        add(panel);

        revalidate();
        repaint();

        backButton.addActionListener(e -> showMainMenu());
    }

    private void showLogin() {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER LOGIN");
        JLabel usernameLabel = new JLabel("Username:");

        JTextField usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(200, 30));

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        backButton.addActionListener(e -> showTraderMenu());
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            Trader trader = traderService.findTraderByUsername(username);

            if (trader == null)
                JOptionPane.showMessageDialog(this, "Trader not found.");
            else
                showTraderDashboard(trader, false);
        });

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(50));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(30));
        panel.add(usernameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(usernameField);

        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);

        panel.add(Box.createVerticalStrut(15));
        panel.add(backButton);

        add(panel);

        revalidate();
        repaint();
    }

    private void showCreateAccount() {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("CREATE TRADER ACCOUNT");

        JLabel firstNameLabel = new JLabel("First name:");
        JTextField firstNameField = new JTextField(15);

        JLabel surnameLabel = new JLabel("Surname:");
        JTextField surnameField = new JTextField(15);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        firstNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        firstNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        surnameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        surnameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        firstNameField.setMaximumSize(new Dimension(200, 30));
        surnameField.setMaximumSize(new Dimension(200, 30));
        usernameField.setMaximumSize(new Dimension(200, 30));

        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(15));
        panel.add(firstNameLabel);
        panel.add(firstNameField);

        panel.add(Box.createVerticalStrut(10));
        panel.add(surnameLabel);
        panel.add(surnameField);

        panel.add(Box.createVerticalStrut(10));
        panel.add(usernameLabel);
        panel.add(usernameField);

        panel.add(Box.createVerticalStrut(15));
        panel.add(createButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);

        backButton.addActionListener(e -> showTraderMenu());

        createButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String username = usernameField.getText().trim();

            if (firstName.isEmpty() || surname.isEmpty() || username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            if (!username.matches("[a-zA-Z0-9_]+")) {
                JOptionPane.showMessageDialog(this, "Username can contain only letters, numbers, and underscores.");
                return;
            }

            if (traderService.findTraderByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
                return;
            }

            int id = traderService.generateUniqueId();

            Trader trader = new Trader(id, username, firstName, surname, 10000);

            traderService.addTrader(trader);
            traderPersistenceService.saveTrader(trader);

            showTraderDashboard(trader, true);
        });

        add(panel);
        revalidate();
        repaint();
    }

    private void showTraderDashboard(Trader trader, boolean newAccount) {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER DASHBOARD");

        JLabel successLabel = new JLabel(" ");

        if (newAccount) {
            successLabel.setText("Account created successfully!");
            successLabel.setForeground(new java.awt.Color(0, 128, 0));
        }

        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Welcome, " + trader.getFullName());

        JLabel balanceLabel = new JLabel("Balance: $" + String.format(java.util.Locale.US, "%.2f", trader.getBalance()));

        JButton marketButton = new JButton("View Market");
        JButton portfolioButton = new JButton("Portfolio");
        JButton transactionsButton = new JButton("Transactions");
        JButton logoutButton = new JButton("Logout");

        marketButton.addActionListener(e -> showMarket(trader));
        portfolioButton.addActionListener(e -> showPortfolio(trader));
        logoutButton.addActionListener(e -> showTraderMenu());

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        marketButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        portfolioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(40));
        panel.add(titleLabel);

        if (newAccount) {
            panel.add(Box.createVerticalStrut(10));
            panel.add(successLabel);
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(nameLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(balanceLabel);

        panel.add(Box.createVerticalStrut(30));
        panel.add(marketButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(portfolioButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(transactionsButton);

        panel.add(Box.createVerticalStrut(20));
        panel.add(logoutButton);

        add(panel);

        revalidate();
        repaint();
    }

    private void showMarket(Trader trader) {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("STOCK MARKET");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel balanceLabel = new JLabel("Available balance: $" + String.format(java.util.Locale.US, "%.2f", trader.getBalance()));

        String[] columnNames = {"Symbol", "Company", "Price"};

        java.util.List<Stock> stocks = marketService.getAllStocks();

        Object[][] data = new Object[stocks.size()][3];

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);

            data[i][0] = stock.getSymbol();
            data[i][1] = stock.getCompanyName();
            data[i][2] = "$" + stock.getCurrentPrice();
        }

        JTable stockTable = new JTable(data, columnNames);

        JScrollPane scrollPane = new JScrollPane(stockTable);

        JButton backButton = new JButton("Back");
        JButton buyButton = new JButton("Buy Stock");
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(java.awt.Color.RED);


        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> showTraderDashboard(trader, false));
        buyButton.addActionListener(e -> {
            int selectedRow = stockTable.getSelectedRow();

            if (selectedRow == -1) {
                errorLabel.setText("Please select a stock first.");
                return;
            }
            errorLabel.setText(" ");

            Stock selectedStock = stocks.get(selectedRow);

            BuyStockDialog dialog = new BuyStockDialog(this, selectedStock, trader, tradingService, traderPersistenceService, transactionService
            );
            dialog.setVisible(true);
        });


        panel.add(Box.createVerticalStrut(30));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(balanceLabel);

        panel.add(Box.createVerticalStrut(20));
        panel.add(scrollPane);

        panel.add(Box.createVerticalStrut(15));
        panel.add(buyButton);

        panel.add(Box.createVerticalStrut(5));
        panel.add(errorLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);

        panel.add(Box.createVerticalStrut(20));


        add(panel);

        revalidate();
        repaint();
    }

    private void showPortfolio(Trader trader) {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("MY PORTFOLIO");

        JLabel balanceLabel = new JLabel("Available balance: $" + String.format(java.util.Locale.US, "%.2f", trader.getBalance())
        );

        String[] columnNames = {"Symbol", "Company", "Shares", "Price", "Value"};

        java.util.Map<Stock, Integer> holdings = trader.getPortfolio().getHoldings();

        Object[][] data = new Object[holdings.size()][5];

        int i = 0;
        double portfolioValue = 0;

        for (Stock stock : holdings.keySet()) {
            int quantity = holdings.get(stock);
            double value = quantity * stock.getCurrentPrice();

            data[i][0] = stock.getSymbol();
            data[i][1] = stock.getCompanyName();
            data[i][2] = quantity;
            data[i][3] = "$" + String.format(java.util.Locale.US, "%.2f", stock.getCurrentPrice());
            data[i][4] = "$" + String.format(java.util.Locale.US, "%.2f", value);

            portfolioValue += value;
            i++;
        }

        JTable portfolioTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(portfolioTable);

        JLabel portfolioValueLabel = new JLabel("Portfolio value: $" + String.format(java.util.Locale.US, "%.2f", portfolioValue));

        JLabel totalValueLabel = new JLabel("Total account value: $" + String.format(java.util.Locale.US, "%.2f", trader.getBalance() + portfolioValue)
        );

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showTraderDashboard(trader, false));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        portfolioValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(balanceLabel);

        panel.add(Box.createVerticalStrut(20));
        panel.add(scrollPane);

        panel.add(Box.createVerticalStrut(15));
        panel.add(portfolioValueLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(totalValueLabel);

        panel.add(Box.createVerticalStrut(20));
        panel.add(backButton);

        add(panel);

        revalidate();
        repaint();
    }
}