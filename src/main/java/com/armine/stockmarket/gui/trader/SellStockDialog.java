package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;

public class SellStockDialog extends JDialog {
    private Stock stock;
    private Trader trader;
    private TradingService tradingService;
    private TraderPersistenceService traderPersistenceService;
    private TransactionService transactionService;
    private JTextField quantityField;
    private JLabel errorLabel;
    private JLabel totalLabel;
    private JLabel balanceLabel;
    private JLabel successLabel;
    private JLabel ownedLabel;
    private JButton sellButton;
    private boolean saleCompleted = false;
    private boolean saleSaved = false;

    public SellStockDialog(JFrame parent, Stock stock, Trader trader, TradingService tradingService, TraderPersistenceService traderPersistenceService, TransactionService transactionService) {
        super(parent, "Sell Stock", true);
        if (stock == null || trader == null || tradingService == null || traderPersistenceService == null || transactionService == null)
            throw new IllegalArgumentException("Sell dialog dependencies cannot be null.");

        this.stock = stock;
        this.trader = trader;
        this.tradingService = tradingService;
        this.traderPersistenceService = traderPersistenceService;
        this.transactionService = transactionService;

        setSize(400, 390);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Sell " + stock.getSymbol() + " Stock");
        JLabel priceLabel = new JLabel(stock.getCompanyName() + " - $" + String.format(Locale.US, "%.2f", stock.getCurrentPrice()) + " per share");

        ownedLabel = new JLabel("Shares owned: " + trader.getOwnedShares(stock));
        JLabel quantityLabel = new JLabel("Quantity:");

        quantityField = new JTextField(10);
        quantityField.setMaximumSize(new Dimension(200, 30));
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        totalLabel = new JLabel("Total proceeds: $0.00");
        balanceLabel = new JLabel("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));
        successLabel = new JLabel(" ");
        successLabel.setForeground(new Color(0, 128, 0));

        sellButton = new JButton("Sell Stock");
        sellButton.setEnabled(false);
        JButton cancelButton = new JButton("Cancel");

        JComponent[] components = {
                titleLabel, priceLabel, ownedLabel,
                quantityLabel, quantityField, errorLabel,
                balanceLabel, totalLabel, successLabel,
                sellButton, cancelButton
        };

        for (JComponent component : components) {
            component.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(priceLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(ownedLabel);

        panel.add(Box.createVerticalStrut(15));
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(errorLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(balanceLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(totalLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(successLabel);

        panel.add(Box.createVerticalStrut(15));
        panel.add(sellButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelButton);

        cancelButton.addActionListener(e -> dispose());

        sellButton.addActionListener(e -> {
            if (!saleCompleted) {
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Please enter a valid whole number.");
                    return;
                }
                try {
                    tradingService.sellStock(trader, stock, quantity);
                } catch (InsufficientSharesException | IllegalArgumentException ex) {
                    errorLabel.setText(ex.getMessage());
                    sellButton.setEnabled(false);
                    return;
                }
                saleCompleted = true;
                quantityField.setEditable(false);
                balanceLabel.setText("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));
                ownedLabel.setText("Shares owned: " + trader.getOwnedShares(stock));
                successLabel.setText("Sold " + quantity + " shares of " + stock.getSymbol() + ".");
                cancelButton.setText("Close");
            }

            // Save without executing another sale
            try {
                traderPersistenceService.saveTrader(trader);
                transactionService.saveTransactions(trader);

                saleSaved = true;
                errorLabel.setText(" ");

                sellButton.setVisible(false);

                revalidate();
                repaint();

            } catch (IllegalStateException ex) {
                saleSaved = false;
                errorLabel.setText("Save failed. Please retry.");
                sellButton.setText("Retry Save");
                sellButton.setEnabled(true);
            }
        });

        // Warn before closing when saving failed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                checkUnsavedSale();
            }
        });
        cancelButton.addActionListener(e -> {
            if (checkUnsavedSale()) dispose();
        });

        // Real-time validation
        quantityField.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        updateTotal();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        updateTotal();
                    }
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        updateTotal();
                    }
                }
        );
        add(panel);
    }

    private boolean checkUnsavedSale() {
        if (saleCompleted && !saleSaved) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Your sale has not been fully saved. Close anyway?",
                    "Unsaved Sale",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            return choice == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void updateTotal() {
        if (saleCompleted) return;
        String input = quantityField.getText().trim();
        sellButton.setEnabled(false);
        if (input.isEmpty()) {
            errorLabel.setText(" ");
            totalLabel.setText("Total proceeds: $0.00");
            return;
        }
        try {
            int quantity = Integer.parseInt(input);
            tradingService.validateSell(trader, stock, quantity);
            double total = quantity * stock.getCurrentPrice();
            totalLabel.setText("Total proceeds: $" + String.format(Locale.US, "%.2f", total));
            errorLabel.setText(" ");
            sellButton.setEnabled(true);
        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid whole number.");
            totalLabel.setText("Total proceeds: $0.00");
        } catch (InsufficientSharesException | IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            totalLabel.setText("Total proceeds: $0.00");
        }
    }
}
