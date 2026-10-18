package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.TraderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;
import java.util.Map;
/**
 * Provides the graphical interface for viewing registered traders.
 *
 * Displays trader account information in a read-only table,
 * including trader IDs, usernames, full names, cash balances,
 * total owned shares, and portfolio values.
 *
 * Administrators can select a trader to inspect their stock
 * holdings in a separate dialog or refresh the displayed data.
 *
 * Trader information is retrieved through TraderService.
 * This panel provides viewing functionality and does not
 * directly modify trader accounts or portfolios.
 */
public class ManageTradersPanel extends JPanel {
    /** Service used to retrieve trader accounts. */
    private TraderService traderService;
    /** Table model holding displayed trader data. */
    private DefaultTableModel tableModel;
    /** Table displaying registered trader accounts. */
    private JTable tradersTable;
    /** Label displaying trader management errors. */
    private JLabel errorLabel;
    /**
     * Creates the trader management panel.
     *
     * Initializes a read-only table displaying registered
     * traders and their account information.
     *
     * Provides buttons for viewing a selected trader's
     * portfolio, refreshing the table, and returning
     * to the administrator dashboard.
     *
     * If no trader is selected or the selected trader
     * is no longer available, an error message is displayed.
     *
     * The table is populated when the panel is created.
     *
     * @param traderService the service providing registered
     *        trader accounts and account lookup operations
     * @param onBack the callback used to return to the
     *        administrator dashboard
     * @throws IllegalArgumentException if traderService
     *         or onBack is null
     */
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
    /**
     * Refreshes the table of registered traders.
     *
     * Clears the existing rows and retrieves all trader
     * accounts from TraderService.
     *
     * For each trader, calculates the total number of
     * owned shares by summing the quantities of all
     * stocks in their portfolio.
     *
     * Displays the trader's account information, cash
     * balance, total shares, and current portfolio value.
     *
     * Monetary amounts are formatted to two decimal places.
     */
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
    /**
     * Displays the selected trader's portfolio in a dialog.
     *
     * Retrieves the trader's holdings and constructs a
     * text summary containing the trader's full name,
     * owned stock symbols, share quantities, and the
     * current value of each holding.
     *
     * If the trader owns no stocks, an informational
     * message is displayed instead.
     *
     * The portfolio details are shown in a read-only
     * text area inside a JOptionPane dialog.
     *
     * @param trader the trader whose portfolio
     *        information will be displayed
     */
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
    /**
     * Displays an error message in the trader management panel.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }
    /**
     * Clears the currently displayed error message.
     */
    private void clearError() {
        errorLabel.setText(" ");
    }
}
