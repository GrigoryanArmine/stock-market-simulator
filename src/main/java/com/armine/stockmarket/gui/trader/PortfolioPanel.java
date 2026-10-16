package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PortfolioPanel extends JPanel {
    public PortfolioPanel(JFrame parent, Trader trader, TradingService tradingService, TraderPersistenceService traderPersistenceService, TransactionService transactionService, Runnable onBack, Runnable onRefresh) {
        if (trader == null || tradingService == null || traderPersistenceService == null || transactionService == null || onBack == null || onRefresh == null)
            throw new IllegalArgumentException("Portfolio panel dependencies cannot be null.");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("MY PORTFOLIO");

        JLabel balanceLabel = new JLabel("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));

        String[] columnNames = {"Symbol", "Company", "Shares", "Price", "Value"};

        // Obtain a snapshot of the trader's holdings
        Map<Stock, Integer> holdings = trader.getPortfolio().getHoldings();

        // Keep stocks in a consistent alphabetical order
        List<Stock> displayedStocks = new ArrayList<>(holdings.keySet());
        displayedStocks.sort(Comparator.comparing(Stock::getSymbol));
        Object[][] data = new Object[displayedStocks.size()][5];
        double portfolioValue = 0;

        // Fill the table with portfolio holdings
        for (int i = 0; i < displayedStocks.size(); i++) {
            Stock stock = displayedStocks.get(i);
            int quantity = holdings.get(stock);

            double price = stock.getCurrentPrice();
            double value = quantity * price;

            data[i][0] = stock.getSymbol();
            data[i][1] = stock.getCompanyName();
            data[i][2] = quantity;
            data[i][3] = "$" + String.format(Locale.US, "%.2f", price);
            data[i][4] = "$" + String.format(Locale.US, "%.2f", value);
            portfolioValue += value;
        }

        // Read-only portfolio table
        JTable portfolioTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        portfolioTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(portfolioTable);

        JLabel portfolioValueLabel = new JLabel("Portfolio value: $" + String.format(Locale.US, "%.2f", portfolioValue));

        JLabel totalValueLabel = new JLabel("Total account value: $" + String.format(Locale.US, "%.2f", trader.getBalance() + portfolioValue));

        JButton sellButton = new JButton("Sell Stock");
        JButton backButton = new JButton("Back");

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        if (displayedStocks.isEmpty()) sellButton.setEnabled(false);

        backButton.addActionListener(e -> onBack.run());

        sellButton.addActionListener(e -> {
            int selectedRow = portfolioTable.getSelectedRow();

            if (selectedRow == -1) {
                errorLabel.setText("Please select a stock first.");
                return;
            }

            errorLabel.setText(" ");

            // Convert view row to model row
            int modelRow = portfolioTable.convertRowIndexToModel(selectedRow);
            Stock selectedStock = displayedStocks.get(modelRow);
            SellStockDialog dialog = new SellStockDialog(
                    parent,
                    selectedStock,
                    trader,
                    tradingService,
                    traderPersistenceService,
                    transactionService
            );
            dialog.setVisible(true);

            // Refresh portfolio after closing the dialog
            onRefresh.run();
        });

        // Align components
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        portfolioValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        sellButton.setPreferredSize(new Dimension(130, 35));
        backButton.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(sellButton);
        buttonPanel.add(backButton);

        // Build the interface
        add(Box.createVerticalStrut(20));
        add(titleLabel);

        add(Box.createVerticalStrut(10));
        add(balanceLabel);

        add(Box.createVerticalStrut(20));
        add(scrollPane);

        add(Box.createVerticalStrut(15));
        add(portfolioValueLabel);

        add(Box.createVerticalStrut(10));
        add(totalValueLabel);

        add(Box.createVerticalStrut(10));
        add(errorLabel);

        add(Box.createVerticalStrut(5));
        add(buttonPanel);

        add(Box.createVerticalStrut(15));
    }
}
