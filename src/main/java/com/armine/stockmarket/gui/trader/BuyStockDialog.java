package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;
import com.armine.stockmarket.exception.InsufficientFundsException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;

public class BuyStockDialog extends JDialog {
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
    private JButton buyButton;
    private boolean purchaseCompleted = false;

    public BuyStockDialog(JFrame parent, Stock stock, Trader trader, TradingService tradingService, TraderPersistenceService traderPersistenceService, TransactionService transactionService) {
        super(parent, "Buy Stock", true);
        if (stock == null || trader == null || tradingService == null || traderPersistenceService == null || transactionService == null)
            throw new IllegalArgumentException("Buy dialog dependencies cannot be null.");

        this.stock = stock;
        this.trader = trader;
        this.tradingService = tradingService;
        this.traderPersistenceService = traderPersistenceService;
        this.transactionService = transactionService;

        setSize(400, 360);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Buy " + stock.getSymbol() + " Stock");
        JLabel priceLabel = new JLabel(stock.getCompanyName() + " - $" + String.format(Locale.US, "%.2f", stock.getCurrentPrice()) + " per share");

        JLabel quantityLabel = new JLabel("Quantity:");

        quantityField = new JTextField(10);
        quantityField.setMaximumSize(new Dimension(200, 30));

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        totalLabel = new JLabel("Total cost: $0.00");
        balanceLabel = new JLabel("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));
        successLabel = new JLabel(" ");
        successLabel.setForeground(new Color(0, 128, 0));
        buyButton = new JButton("Buy Stock");
        buyButton.setEnabled(false);

        JButton cancelButton = new JButton("Cancel");

        JComponent[] components = {
                titleLabel, priceLabel, quantityLabel,
                quantityField, errorLabel, totalLabel,
                balanceLabel, successLabel,
                buyButton, cancelButton
        };

        for (JComponent component : components) {
            component.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(10));
        panel.add(priceLabel);

        panel.add(Box.createVerticalStrut(20));
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
        panel.add(buyButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelButton);

        cancelButton.addActionListener(e -> dispose());

        buyButton.addActionListener(e -> {
            if (!purchaseCompleted) {
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Please enter a valid whole number.");
                    return;
                }
                try {
                    tradingService.buyStock(trader, stock, quantity);
                } catch (InsufficientFundsException | IllegalArgumentException ex) {
                    errorLabel.setText(ex.getMessage());
                    buyButton.setEnabled(false);
                    return;
                }
                purchaseCompleted = true;
                quantityField.setEditable(false);
                balanceLabel.setText("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));
                successLabel.setText("Purchased " + quantity + " shares of " + stock.getSymbol() + ".");
                cancelButton.setText("Close");
            }
            // Save, without repeating the purchase
            try {
                traderPersistenceService.saveTrader(trader);
                transactionService.saveTransactions(trader);

                errorLabel.setText(" ");
                buyButton.setVisible(false);

                revalidate();
                repaint();

            } catch (IllegalStateException ex) {
                errorLabel.setText("Save failed. Please retry saving.");
                buyButton.setText("Retry Save");
                buyButton.setEnabled(true);
            }
        });

        quantityField.getDocument().addDocumentListener(new DocumentListener() {
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

    private void updateTotal() {
        if (purchaseCompleted) return;
        String input = quantityField.getText().trim();
        buyButton.setEnabled(false);
        if (input.isEmpty()) {
            errorLabel.setText(" ");
            totalLabel.setText("Total cost: $0.00");
            return;
        }
        try {
            int quantity = Integer.parseInt(input);
            tradingService.validateBuy(trader, stock, quantity);
            double total = quantity * stock.getCurrentPrice();

            totalLabel.setText("Total cost: $" + String.format(Locale.US, "%.2f", total));
            errorLabel.setText(" ");
            buyButton.setEnabled(true);

        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid whole number.");
            totalLabel.setText("Total cost: $0.00");
        } catch (InsufficientFundsException | IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
            totalLabel.setText("Total cost: $0.00");
        }
    }
}
