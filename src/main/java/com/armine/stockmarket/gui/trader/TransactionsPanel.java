package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
/**
 * Displays the transaction history of an authenticated trader.
 *
 * Shows historical stock purchases and sales in a table
 * containing transaction dates, types, stock symbols,
 * share quantities, execution prices, and total amounts.
 *
 * Transactions are initially displayed from newest to oldest.
 * The table supports sorting by column and uses custom
 * renderers to format dates and monetary values.
 *
 * Historical execution prices are displayed independently
 * of the stocks' current market prices.
 *
 * Navigation back to the trader dashboard is handled
 * through a Runnable callback.
 */
public class TransactionsPanel extends JPanel {
    /**
     * Creates the transaction history panel for a trader.
     *
     * Retrieves the trader's transaction history and sorts
     * a copy of the transaction list by date and time
     * in descending order.
     *
     * Displays transactions in a read-only JTable with
     * appropriate column data types to support sorting.
     *
     * Dates are formatted as yyyy-MM-dd HH:mm, while
     * monetary values are displayed in US dollar format
     * with two decimal places.
     *
     * If the trader has no recorded transactions,
     * an informational message is displayed.
     *
     * @param trader the trader whose transaction history
     *        will be displayed
     * @param onBack the callback used to return
     *        to the trader dashboard
     * @throws IllegalArgumentException if trader
     *         or onBack is null
     */
    public TransactionsPanel(Trader trader, Runnable onBack) {
        if (trader == null || onBack == null)
            throw new IllegalArgumentException("Trader and onBack cannot be null.");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("TRANSACTION HISTORY");

        String[] columns = {"Date & Time", "Type", "Stock", "Quantity", "Price per Share", "Total"};

        // Read-only model with correct column types
        DefaultTableModel tableModel =
                new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                    @Override
                    public Class<?> getColumnClass(int column) {
                        return switch (column) {
                            case 0 -> LocalDateTime.class;
                            case 3 -> Integer.class;
                            case 4, 5 -> Double.class;
                            default -> String.class;
                        };
                    }
                };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Get a copy and display newest transactions first
        List<Transaction> transactions = trader.getTransactions();
        transactions.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        for (Transaction transaction : transactions) {
            Object[] row = {
                    transaction.getDateTime(),
                    transaction.getType().toString(),
                    transaction.getStock().getSymbol(),
                    transaction.getQuantity(),
                    transaction.getPricePerShare(),
                    transaction.getTotalAmount()
            };
            tableModel.addRow(row);
        }

        JTable transactionTable = new JTable(tableModel);
        transactionTable.setFillsViewportHeight(true);
        transactionTable.setAutoCreateRowSorter(true);

        // Format dates while keeping them sortable
        DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof LocalDateTime) {
                    setText(((LocalDateTime) value).format(formatter));
                } else {
                    setText("");
                }
            }
        };
        transactionTable.setDefaultRenderer(LocalDateTime.class, dateRenderer);

        // Format monetary amounts while keeping numeric sorting
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number) {
                    setText(String.format(Locale.US, "$%,.2f", ((Number) value).doubleValue()));
                } else {
                    setText("");
                }
            }
        };

        moneyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        transactionTable.setDefaultRenderer(Double.class, moneyRenderer);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        JLabel emptyLabel = new JLabel(" ");
        if (transactions.isEmpty()) emptyLabel.setText("No transactions yet.");

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 35));

        backButton.addActionListener(e -> onBack.run());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        buttonPanel.add(backButton);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Build interface
        add(Box.createVerticalStrut(20));
        add(titleLabel);

        add(Box.createVerticalStrut(10));
        add(emptyLabel);

        add(Box.createVerticalStrut(10));
        add(scrollPane);

        add(Box.createVerticalStrut(10));
        add(buttonPanel);

        add(Box.createVerticalStrut(15));
    }
}
