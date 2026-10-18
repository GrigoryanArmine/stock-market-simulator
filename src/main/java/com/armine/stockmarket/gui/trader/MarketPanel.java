package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
/**
 * Displays the stock market interface for an authenticated trader.
 *
 * Shows the trader's available cash balance and a table
 * containing all stocks currently available in the market.
 *
 * Stocks are displayed alphabetically by symbol, including
 * their company names and current market prices.
 *
 * Traders can select a stock and open BuyStockDialog
 * to purchase shares.
 *
 * Navigation and screen refreshing are handled through
 * Runnable callbacks provided by MainFrame.
 */
public class MarketPanel extends JPanel {
    /**
     * Creates the market panel for the specified trader.
     *
     * Retrieves stocks from MarketService and displays them
     * in a non-editable JTable with single-row selection.
     *
     * The Buy Stock button opens BuyStockDialog for the
     * selected stock. After the dialog closes, the
     * onRefresh callback is invoked to refresh the market view.
     *
     * If no stocks are available, the buy button is disabled.
     * If the user attempts to buy without selecting a stock,
     * an error message is displayed.
     *
     * @param parent the parent window for the stock purchase dialog
     * @param trader the authenticated trader viewing the market
     * @param marketService the service providing available stocks
     * @param tradingService the service handling stock purchases
     * @param traderPersistenceService the service saving trader data
     * @param transactionService the service saving transaction history
     * @param onBack the callback used to return to the trader dashboard
     * @param onRefresh the callback used to refresh the market panel
     * @throws IllegalArgumentException if trader, a required service,
     *         or a navigation callback is null
     */
    public MarketPanel(
            JFrame parent,
            Trader trader,
            MarketService marketService,
            TradingService tradingService,
            TraderPersistenceService traderPersistenceService,
            TransactionService transactionService,
            Runnable onBack,
            Runnable onRefresh) {

        if (trader == null || marketService == null || tradingService == null || traderPersistenceService == null || transactionService == null || onBack == null || onRefresh == null)
            throw new IllegalArgumentException("Market panel dependencies cannot be null.");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Title and balance
        JLabel titleLabel = new JLabel("STOCK MARKET");
        JLabel balanceLabel = new JLabel("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));

        // Market table
        String[] columnNames = {"Symbol", "Company", "Price"};
        List<Stock> stocks = new ArrayList<>(marketService.getAllStocks());
        stocks.sort(Comparator.comparing(Stock::getSymbol));

        Object[][] data = new Object[stocks.size()][3];

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            data[i][0] = stock.getSymbol();
            data[i][1] = stock.getCompanyName();
            data[i][2] = "$" + String.format(Locale.US, "%.2f", stock.getCurrentPrice());
        }
        JTable stockTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(stockTable);

        // Buttons
        JButton buyButton = new JButton("Buy Stock");
        JButton backButton = new JButton("Back");

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        // Disable buying when the market is empty
        if (stocks.isEmpty()) {
            buyButton.setEnabled(false);
            errorLabel.setText("No stocks available.");
        }

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
            BuyStockDialog dialog = new BuyStockDialog(
                    parent,
                    selectedStock,
                    trader,
                    tradingService,
                    traderPersistenceService,
                    transactionService
            );
            dialog.setVisible(true);
            // Refresh market after closing the dialog
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
