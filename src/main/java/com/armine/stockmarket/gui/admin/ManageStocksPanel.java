package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ManageStocksPanel extends JPanel {
    private MarketService marketService;
    private TraderService traderService;
    private DefaultTableModel tableModel;
    private JTable stocksTable;
    private JLabel errorLabel;

    public ManageStocksPanel(MarketService marketService, TraderService traderService, Runnable onBack) {
        if (marketService == null || traderService == null || onBack == null)
            throw new IllegalArgumentException("Manage stocks dependencies cannot be null.");

        this.marketService = marketService;
        this.traderService = traderService;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titleLabel = new JLabel("MANAGE STOCKS");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Stock table
        String[] columns = {"Symbol", "Company", "Current Price ($)"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stocksTable = new JTable(tableModel);
        stocksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stocksTable.setFillsViewportHeight(true);

        add(new JScrollPane(stocksTable), BorderLayout.CENTER);

        // Error message
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Buttons
        JButton addButton = new JButton("Add Stock");
        JButton updateButton = new JButton("Update Price");
        JButton removeButton = new JButton("Remove Stock");
        JButton backButton = new JButton("Back");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(errorLabel);
        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Add stock
        addButton.addActionListener(e -> {
            clearError();
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            AddStockDialog dialog = new AddStockDialog(parent, marketService, this::refreshTable);
            dialog.setVisible(true);
        });

        // Update stock price
        updateButton.addActionListener(e -> {
            String symbol = getSelectedSymbol();
            if (symbol == null) {
                showError("Please select a stock first.");
                return;
            }
            try {
                Stock stock = marketService.requireStock(symbol);
                clearError();
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                UpdateStockPriceDialog dialog = new UpdateStockPriceDialog(parent, marketService, stock, this::refreshTable);
                dialog.setVisible(true);
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        // Remove stock
        removeButton.addActionListener(e -> {
            String symbol = getSelectedSymbol();
            if (symbol == null) {
                showError("Please select a stock first.");
                return;
            }
            clearError();
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove "
                            + symbol + " from the market?",
                    "Confirm Stock Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) return;
            try {
                marketService.removeStock(symbol, traderService);
                refreshTable();
                clearError();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                refreshTable();
                showError(ex.getMessage());
            }
        });

        // Back
        backButton.addActionListener(e -> onBack.run());

        // Clear error when selecting another stock
        stocksTable.getSelectionModel().addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) clearError();

        });
        refreshTable();
    }

    private String getSelectedSymbol() {
        int selectedRow = stocksTable.getSelectedRow();
        if (selectedRow == -1) return null;
        int modelRow = stocksTable.convertRowIndexToModel(selectedRow);
        return tableModel.getValueAt(modelRow, 0).toString();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        List<Stock> stocks = marketService.getAllStocks();
        stocks.sort(Comparator.comparing(Stock::getSymbol));
        for (Stock stock : stocks) {
            tableModel.addRow(new Object[]{
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    String.format(Locale.US, "%.2f", stock.getCurrentPrice())
            });
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText(" ");
    }
}
