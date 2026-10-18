package com.armine.stockmarket.gui.trader;

import com.armine.stockmarket.model.*;
import com.armine.stockmarket.service.*;
import com.armine.stockmarket.exception.InsufficientFundsException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;
/**
 * Provides a modal dialog for purchasing stock shares.
 *
 * Displays the selected stock's information, current price,
 * the trader's available balance, and the total purchase cost.
 *
 * The total cost and purchase eligibility are updated
 * automatically when the entered quantity changes.
 *
 * Purchases are executed through TradingService, while trader
 * account information and transaction history are saved
 * using their corresponding persistence services.
 *
 * If saving fails after a successful purchase, the dialog
 * allows the user to retry saving without executing
 * the purchase again.
 */
public class BuyStockDialog extends JDialog {
    /** Stock selected for purchase. */
    private Stock stock;
    /** Trader performing the purchase. */
    private Trader trader;
    /** Service validating and executing the purchase. */
    private TradingService tradingService;
    /** Service saving the updated trader account. */
    private TraderPersistenceService traderPersistenceService;
    /** Service saving recorded transactions. */
    private TransactionService transactionService;
    /** Input field for the number of shares to buy. */
    private JTextField quantityField;
    /** Label displaying purchase or saving errors. */
    private JLabel errorLabel;
    /** Label displaying the calculated purchase cost. */
    private JLabel totalLabel;
    /** Label displaying the trader's cash balance. */
    private JLabel balanceLabel;
    /** Label confirming a completed purchase. */
    private JLabel successLabel;
    /** Button executing a purchase or retrying a save. */
    private JButton buyButton;
    /** Whether the purchase has already been executed. */
    private boolean purchaseCompleted = false;
    /**
     * Creates a modal stock purchase dialog.
     *
     * Displays the selected stock's symbol, company name,
     * current market price, and the trader's available balance.
     *
     * The purchase button is initially disabled and becomes
     * enabled when the entered quantity passes validation.
     *
     * When a purchase succeeds, the trader's balance,
     * portfolio holdings, and transaction history are updated.
     * The dialog then attempts to save the account and transactions.
     *
     * If saving fails, the purchase is not repeated. Instead,
     * the purchase button changes to "Retry Save".
     *
     * @param parent the parent window for the modal dialog
     * @param stock the stock selected for purchase
     * @param trader the trader purchasing the stock
     * @param tradingService the service processing stock purchases
     * @param traderPersistenceService the service saving trader data
     * @param transactionService the service saving transaction history
     * @throws IllegalArgumentException if stock, trader,
     *         or any required service is null
     */
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
    /**
     * Validates the entered share quantity and updates
     * the displayed total purchase cost.
     *
     * Called whenever the quantity field changes.
     *
     * The method parses the quantity and delegates purchase
     * validation to TradingService, including checking
     * available funds and share quantity limits.
     *
     * If validation succeeds, the total cost is displayed
     * and the purchase button is enabled.
     *
     * If validation fails, an error message is displayed,
     * the total cost is reset to zero, and buying is disabled.
     *
     * Once a purchase has completed, this method returns
     * without changing the purchase controls.
     */
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
