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
/**
 * Displays the stock portfolio of an authenticated trader.
 *
 * Shows the trader's available cash balance, owned stocks,
 * share quantities, current market prices, and holding values.
 *
 * The panel calculates the total portfolio value using
 * current market prices and displays the total account value,
 * which includes both cash and stock holdings.
 *
 * Traders can select an owned stock and open SellStockDialog
 * to sell shares.
 *
 * Navigation and screen refreshing are managed through
 * Runnable callbacks supplied by MainFrame.
 */
public class PortfolioPanel extends JPanel {
    /**
     * Creates the portfolio panel for the specified trader.
     *
     * Retrieves the trader's holdings and displays them
     * in a non-editable JTable sorted by stock symbol.
     *
     * Each table row shows the stock symbol, company name,
     * number of shares, current price, and holding value.
     *
     * The total portfolio value is calculated as the sum
     * of each stock's current price multiplied by its
     * owned quantity.
     *
     * The total account value is calculated by adding
     * the trader's available balance to the portfolio value.
     *
     * Selecting a stock and clicking Sell Stock opens
     * SellStockDialog. After the dialog closes, the
     * onRefresh callback is invoked to update the portfolio.
     *
     * If the portfolio is empty, the Sell Stock button
     * is disabled.
     *
     * @param parent the parent window for the selling dialog
     * @param trader the trader whose portfolio is displayed
     * @param tradingService the service processing stock sales
     * @param traderPersistenceService the service saving trader data
     * @param transactionService the service saving transaction history
     * @param onBack the callback used to return to the trader dashboard
     * @param onRefresh the callback used to refresh the portfolio panel
     * @throws IllegalArgumentException if trader, a required service,
     *         or a navigation callback is null
     */
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
