package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TransactionService;

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
    private JButton sellButton;

    public SellStockDialog(JFrame parent, Stock stock, Trader trader, TradingService tradingService, TraderPersistenceService traderPersistenceService, TransactionService transactionService) {
        super(parent, "Sell Stock", true);
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
        JLabel ownedLabel = new JLabel("Shares owned: " + trader.getPortfolio().getQuantity(stock));
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

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ownedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityField.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sellButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter a valid whole number.");
                return;
            }

            try {
                tradingService.sellStock(trader, stock, quantity);
                traderPersistenceService.saveTrader(trader);
                transactionService.saveTransactions(trader);

                errorLabel.setText(" ");
                successLabel.setText("Successfully sold " + quantity + " shares of " + stock.getSymbol() + "!");
                balanceLabel.setText("Available balance: $" + String.format(Locale.US, "%.2f", trader.getBalance()));
                ownedLabel.setText("Shares owned: " + trader.getPortfolio().getQuantity(stock));
                sellButton.setVisible(false);
                quantityField.setEditable(false);
                cancelButton.setText("Close");
            } catch (InsufficientSharesException | IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateTotal(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateTotal(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateTotal(); }
        });

        add(panel);
    }


    private void updateTotal() {
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
