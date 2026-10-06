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

public class MainFrame extends JFrame {
    private TraderService traderService;
    private MarketService marketService;
    private TradingService tradingService;

    public MainFrame(TraderService traderService, MarketService marketService, TradingService tradingService) {
        this.traderService = traderService;
        this.marketService = marketService;
        this.tradingService = tradingService;
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
                showTraderDashboard(trader);
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

    private void showTraderDashboard(Trader trader) {
        getContentPane().removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("TRADER DASHBOARD");

        JLabel nameLabel = new JLabel("Welcome, " + trader.getFullName());

        JLabel balanceLabel = new JLabel("Balance: $" + trader.getBalance());

        JButton marketButton = new JButton("View Market");
        JButton portfolioButton = new JButton("Portfolio");
        JButton transactionsButton = new JButton("Transactions");
        JButton logoutButton = new JButton("Logout");

        marketButton.addActionListener(e -> showMarket(trader));
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

        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> showTraderDashboard(trader));

        panel.add(Box.createVerticalStrut(30));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(20));
        panel.add(scrollPane);

        panel.add(Box.createVerticalStrut(15));
        panel.add(buyButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);

        panel.add(Box.createVerticalStrut(20));

        buyButton.addActionListener(e -> {
            int selectedRow = stockTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a stock first.");
                return;
            }

            Stock selectedStock = stocks.get(selectedRow);

            JOptionPane.showMessageDialog(this, "Selected: " + selectedStock.getSymbol());
        });

        add(panel);

        revalidate();
        repaint();
    }
}