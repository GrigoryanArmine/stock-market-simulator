package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.TraderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;
import java.util.Map;

public class ManageTradersPanel extends JPanel {
    private TraderService traderService;
    private DefaultTableModel tableModel;
    private JTable tradersTable;
    private JLabel errorLabel;

    public ManageTradersPanel(TraderService traderService, Runnable onBack) {
        if (traderService == null || onBack == null)
            throw new IllegalArgumentException("Manage traders dependencies cannot be null.");

        this.traderService = traderService;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titleLabel = new JLabel("MANAGE TRADERS");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Trader table
        String[] columns = {"ID", "Username", "Full Name", "Balance ($)", "Total Shares", "Portfolio Value ($)"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tradersTable = new JTable(tableModel);
        tradersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tradersTable.setFillsViewportHeight(true);

        add(new JScrollPane(tradersTable), BorderLayout.CENTER);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Buttons
        JButton viewPortfolioButton = new JButton("View Portfolio");

        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        buttonPanel.add(viewPortfolioButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(errorLabel);
        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // View portfolio
        viewPortfolioButton.addActionListener(e -> {
            int selectedRow = tradersTable.getSelectedRow();
            if (selectedRow == -1) {
                showError("Please select a trader first.");
                return;
            }
            int modelRow = tradersTable.convertRowIndexToModel(selectedRow);
            int traderId = (int) tableModel.getValueAt(modelRow, 0);
            Trader trader = traderService.findTraderById(traderId);

            if (trader == null) {
                refreshTable();
                showError("This trader is no longer available.");
                return;
            }
            clearError();
            showPortfolio(trader);
        });

        // Refresh
        refreshButton.addActionListener(e -> {refreshTable();clearError();});

        // Back
        backButton.addActionListener(e -> onBack.run());

        // Clear error when selection changes
        tradersTable.getSelectionModel().addListSelectionListener(e -> {if (!e.getValueIsAdjusting()) clearError();});
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Trader trader : traderService.getAllTraders()) {
            Portfolio portfolio = trader.getPortfolio();
            long totalShares = 0;
            for (int quantity : portfolio.getHoldings().values()) {
                totalShares += quantity;
            }

            tableModel.addRow(new Object[]{
                    trader.getId(),
                    trader.getUsername(),
                    trader.getFullName(),
                    String.format(Locale.US, "%.2f", trader.getBalance()),
                    totalShares,
                    String.format(Locale.US, "%.2f", portfolio.getTotalValue())
            });
        }
    }

    private void showPortfolio(Trader trader) {
        StringBuilder details = new StringBuilder();

        details.append("Trader: ").append(trader.getFullName()).append("\n\n");

        Portfolio portfolio = trader.getPortfolio();
        Map<Stock, Integer> holdings = portfolio.getHoldings();

        if (holdings.isEmpty()) {
            details.append("This trader does not own any stocks.");
        } else {
            for (Map.Entry<Stock, Integer> entry : holdings.entrySet()) {
                Stock stock = entry.getKey();
                int quantity = entry.getValue();

                details.append(stock.getSymbol())
                        .append(" - ")
                        .append(quantity)
                        .append(" shares")
                        .append(" - $")
                        .append(String.format(Locale.US, "%.2f", stock.getCurrentPrice() * quantity))
                        .append("\n");
            }
            details.append("\nTotal portfolio value: $").append(String.format(Locale.US, "%.2f", portfolio.getTotalValue()));
        }

        JTextArea detailsArea = new JTextArea(details.toString());

        detailsArea.setEditable(false);
        detailsArea.setRows(10);
        detailsArea.setColumns(35);
        detailsArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(this, new JScrollPane(detailsArea), "Trader Portfolio", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText(" ");
    }
}
