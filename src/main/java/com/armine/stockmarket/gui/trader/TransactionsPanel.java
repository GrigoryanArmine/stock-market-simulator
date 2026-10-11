
package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransactionsPanel extends JPanel {

    public TransactionsPanel(Trader trader, Runnable onBack) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("TRANSACTION HISTORY");

        // Table columns
        String[] columns = {"Date & Time", "Type", "Stock", "Quantity", "Price per Share", "Total"};

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Load transaction history
        for (Transaction transaction : trader.getTransactions()) {
            Object[] row = {
                    transaction.getDateTime().format(formatter),
                    transaction.getType(),
                    transaction.getStock().getSymbol(),
                    transaction.getQuantity(),
                    String.format(Locale.US, "$%.2f", transaction.getPricePerShare()),
                    String.format(Locale.US, "$%.2f", transaction.getTotalAmount())
            };
            tableModel.addRow(row);
        }

        JTable transactionTable = new JTable(tableModel);
        transactionTable.setFillsViewportHeight(true);
        transactionTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(transactionTable);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 35));

        backButton.addActionListener(e -> onBack.run());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        buttonPanel.add(backButton);

        // Center title
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Build the interface
        add(Box.createVerticalStrut(20));
        add(titleLabel);

        add(Box.createVerticalStrut(20));
        add(scrollPane);

        add(Box.createVerticalStrut(10));
        add(buttonPanel);

        add(Box.createVerticalStrut(15));
    }
}
