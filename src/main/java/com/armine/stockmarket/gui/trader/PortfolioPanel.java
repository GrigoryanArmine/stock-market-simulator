
package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Map;

public class PortfolioPanel extends JPanel {
    public PortfolioPanel(
            JFrame parent,
            Trader trader,
            TradingService tradingService,
            TraderPersistenceService traderPersistenceService,
            TransactionService transactionService,
            Runnable onBack,
            Runnable onRefresh) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("MY PORTFOLIO");

        JLabel balanceLabel = new JLabel("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));

        String[] columnNames = {"Symbol", "Company", "Shares", "Price", "Value"};

        Map<Stock, Integer> holdings = trader.getPortfolio().getHoldings();

        Object[][] data = new Object[holdings.size()][5];

        int i = 0;
        double portfolioValue = 0;

        // Fill the table with portfolio holdings
        for (Stock stock : holdings.keySet()) {
            int quantity = holdings.get(stock);
            double value = quantity * stock.getCurrentPrice();

            data[i][0] = stock.getSymbol();
            data[i][1] = stock.getCompanyName();
            data[i][2] = quantity;
            data[i][3] = "$" + String.format(Locale.US, "%.2f", stock.getCurrentPrice());
            data[i][4] = "$" + String.format(Locale.US, "%.2f", value
            );

            portfolioValue += value;
            i++;
        }

        // Create the table outside the loop
        JTable portfolioTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(portfolioTable);

        JLabel portfolioValueLabel = new JLabel("Portfolio value: $" + String.format(Locale.US, "%.2f", portfolioValue));

        JLabel totalValueLabel = new JLabel("Total account value: $" +
                        String.format(Locale.US, "%.2f", trader.getBalance() + portfolioValue)
        );

        JButton sellButton = new JButton("Sell Stock");
        JButton backButton = new JButton("Back");

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        backButton.addActionListener(e -> onBack.run());

        sellButton.addActionListener(e -> {
            int selectedRow = portfolioTable.getSelectedRow();

            if (selectedRow == -1) {
                errorLabel.setText("Please select a stock first.");
                return;
            }

            errorLabel.setText(" ");

            // Convert selected view row to model row
            int modelRow = portfolioTable.convertRowIndexToModel(selectedRow);

            String symbol = (String) portfolioTable.getModel().getValueAt(modelRow, 0);

            Stock selectedStock = null;

            for (Stock ownedStock : holdings.keySet()) {
                if (ownedStock.getSymbol().equals(symbol)) {
                    selectedStock = ownedStock;
                    break;
                }
            }

            if (selectedStock == null) {
                errorLabel.setText("Stock not found in portfolio.");
                return;
            }

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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)
        );

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
