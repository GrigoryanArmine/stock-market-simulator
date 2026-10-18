package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.exception.InsufficientSharesException;
import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;
/**
 * Provides a modal dialog for selling stock shares.
 *
 * Displays the selected stock's information, the trader's
 * available balance, the number of shares owned, and the
 * expected proceeds from the sale.
 *
 * The entered quantity is validated through TradingService,
 * and the total proceeds are updated automatically.
 *
 * After a successful sale, the dialog saves the trader's
 * account information and transaction history.
 *
 * If saving fails, the dialog allows the user to retry
 * saving without executing the sale again.
 */
public class SellStockDialog extends JDialog {
    /** Stock selected for sale. */
    private Stock stock;
    /** Trader performing the sale. */
    private Trader trader;
    /** Service validating and executing the sale. */
    private TradingService tradingService;
    /** Service saving the updated trader account. */
    private TraderPersistenceService traderPersistenceService;
    /** Service saving recorded transactions. */
    private TransactionService transactionService;
    /** Input field for the number of shares to sell. */
    private JTextField quantityField;
    /** Label displaying sale or saving errors. */
    private JLabel errorLabel;
    /** Label displaying the calculated sale proceeds. */
    private JLabel totalLabel;
    /** Label displaying the trader's cash balance. */
    private JLabel balanceLabel;
    /** Label confirming a completed sale. */
    private JLabel successLabel;
    /** Label displaying the number of shares owned. */
    private JLabel ownedLabel;
    /** Button executing a sale or retrying a save. */
    private JButton sellButton;
    /** Whether the sale has already been executed. */
    private boolean saleCompleted = false;
    /** Whether the completed sale was successfully saved. */
    private boolean saleSaved = false;
    /**
     * Creates a modal dialog for selling the specified stock.
     *
     * Displays the stock's current market price, the number
     * of shares owned, and the trader's available cash balance.
     *
     * The Sell Stock button is initially disabled. It becomes
     * enabled when the entered quantity passes validation.
     *
     * After a successful sale, the dialog updates the displayed
     * balance and share quantity, then attempts to save the
     * trader's account and transaction history.
     *
     * If saving fails, the sale remains completed in memory,
     * and the button changes to "Retry Save".
     *
     * @param parent the parent window for the modal dialog
     * @param stock the stock selected for sale
     * @param trader the trader selling shares
     * @param tradingService the service processing stock sales
     * @param traderPersistenceService the service saving trader data
     * @param transactionService the service saving transaction history
     * @throws IllegalArgumentException if stock, trader,
     *         or any required service is null
     */
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
    /**
     * Checks whether a completed sale has been fully saved.
     *
     * If the sale was executed but saving did not succeed,
     * displays a confirmation dialog asking whether the
     * user wants to close despite the unsaved changes.
     *
     * If the sale has not been executed or has already
     * been saved, no confirmation dialog is displayed.
     *
     * @return true if the user confirms closing or no
     *         unsaved sale exists, false if the user
     *         chooses not to close
     */
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
    /**
     * Validates the entered share quantity and updates
     * the expected proceeds from the sale.
     *
     * Called whenever the quantity field changes.
     *
     * The method parses the quantity and uses TradingService
     * to verify that the trader owns enough shares and
     * that the resulting account balance is valid.
     *
     * If validation succeeds, the expected proceeds
     * are displayed and the Sell Stock button is enabled.
     *
     * If validation fails, an error message is displayed,
     * the proceeds are reset to zero, and selling is disabled.
     *
     * Once a sale has completed, this method returns
     * without modifying the sale controls.
     */
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
