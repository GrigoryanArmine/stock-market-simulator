
package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class MarketPanel extends JPanel {
    public MarketPanel(
            JFrame parent,
            Trader trader,
            MarketService marketService,
            TradingService tradingService,
            TraderPersistenceService traderPersistenceService,
            TransactionService transactionService,
            Runnable onBack,
            Runnable onRefresh) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Title and balance
        JLabel titleLabel = new JLabel("STOCK MARKET");
        JLabel balanceLabel = new JLabel("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));

        // Market table
        String[] columnNames = {"Symbol", "Company", "Price"};

        List<Stock> stocks = marketService.getAllStocks();
        Object[][] data = new Object[stocks.size()][3];

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            data[i][0] = stock.getSymbol();
            data[i][1] = stock.getCompanyName();
            data[i][2] = "$" + String.format(Locale.US, "%.2f", stock.getCurrentPrice());
        }

        JTable stockTable = new JTable(data, columnNames);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(stockTable);

        // Buttons
        JButton buyButton = new JButton("Buy Stock");
        JButton backButton = new JButton("Back");

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        // Back to Trader Dashboard
        backButton.addActionListener(e -> onBack.run());

        // Buy selected stock
        buyButton.addActionListener(e -> {
            int selectedRow = stockTable.getSelectedRow();
            if (selectedRow == -1) {
                errorLabel.setText("Please select a stock first.");
                return;
            }

            errorLabel.setText(" ");

            int modelRow = stockTable.convertRowIndexToModel(selectedRow);

            Stock selectedStock = stocks.get(modelRow);

            BuyStockDialog dialog = new BuyStockDialog(parent, selectedStock, trader, tradingService, traderPersistenceService, transactionService);
            dialog.setVisible(true);

            // Refresh after closing the dialog
            onRefresh.run();
        });

        // Center labels
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create horizontal button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        buyButton.setPreferredSize(new Dimension(130, 35));
        backButton.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(buyButton);
        buttonPanel.add(backButton);

        // Build the interface
        add(Box.createVerticalStrut(20));
        add(titleLabel);

        add(Box.createVerticalStrut(10));
        add(balanceLabel);

        add(Box.createVerticalStrut(20));
        add(scrollPane);

        add(Box.createVerticalStrut(10));
        add(errorLabel);

        add(Box.createVerticalStrut(5));
        add(buttonPanel);

        add(Box.createVerticalStrut(15));
    }
}
