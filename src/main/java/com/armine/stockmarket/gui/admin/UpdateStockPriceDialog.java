package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.service.MarketService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;
/**
 * Provides a modal dialog for updating an existing stock's price.
 *
 * Displays the selected stock's symbol, company name, and
 * current market price.
 *
 * Administrators can enter a new price, which is validated
 * automatically as the input changes.
 *
 * Price updates are delegated to MarketService rather than
 * modifying the stock directly.
 *
 * After a successful update, a callback is invoked to
 * refresh the stock management table.
 */
public class UpdateStockPriceDialog extends JDialog {
    /** Service used to update stock prices. */
    private MarketService marketService;
    /** Stock whose price is being updated. */
    private Stock stock;
    /** Input field for the new stock price. */
    private JTextField priceField;
    /** Label displaying price validation errors. */
    private JLabel errorLabel;
    /**
     * Creates a modal dialog for updating a stock's market price.
     *
     * Displays the selected stock's information and provides
     * a text field for entering the new share price.
     *
     * The input is validated as the administrator types.
     * When the update button is clicked, the new price
     * is passed to MarketService for processing.
     *
     * If the update succeeds, the onPriceUpdated callback
     * is executed and the dialog closes.
     *
     * If validation or updating fails, the dialog displays
     * an error message and remains open.
     *
     * @param parent the parent window for the modal dialog
     * @param marketService the service responsible for
     *        managing stock prices
     * @param stock the stock whose price will be updated
     * @param onPriceUpdated the callback invoked after
     *        successfully updating the stock price
     * @throws IllegalArgumentException if marketService,
     *         stock, or onPriceUpdated is null
     */
    public UpdateStockPriceDialog(JFrame parent, MarketService marketService, Stock stock, Runnable onPriceUpdated) {
        super(parent, "Update Stock Price", true);

        if (marketService == null || stock == null || onPriceUpdated == null)
            throw new IllegalArgumentException("Update stock price dependencies cannot be null.");

        this.marketService = marketService;
        this.stock = stock;

        setSize(400, 340);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("UPDATE STOCK PRICE");

        JLabel stockLabel = new JLabel(stock.getSymbol() + " - " + stock.getCompanyName());

        JLabel currentPriceLabel = new JLabel("Current price: $" + String.format(Locale.US, "%.2f", stock.getCurrentPrice()));

        JLabel priceLabel = new JLabel("New Price ($)");

        priceField = new JTextField(15);
        priceField.setMaximumSize(priceField.getPreferredSize());

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        JButton updateButton = new JButton("Update Price");
        JButton cancelButton = new JButton("Cancel");

        JComponent[] components = {titleLabel, stockLabel, currentPriceLabel, priceLabel, priceField, errorLabel, updateButton, cancelButton};

        for (JComponent component : components) {
            component.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        // Real-time price validation
        priceField.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        validatePrice();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        validatePrice();
                    }
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        validatePrice();
                    }
                }
        );

        // Update stock price
        updateButton.addActionListener(e -> {
            String input = priceField.getText().trim();

            if (input.isEmpty()) {
                errorLabel.setText("Please enter a price.");
                return;
            }

            try {
                double newPrice = Double.parseDouble(input);
                marketService.updatePrice(stock.getSymbol(), newPrice);
            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter a valid numeric price.");
                return;
            } catch (IllegalArgumentException | IllegalStateException ex) {
                errorLabel.setText(ex.getMessage());
                currentPriceLabel.setText("Current price: $" + String.format(Locale.US, "%.2f", stock.getCurrentPrice()));
                return;
            }
            onPriceUpdated.run();
            dispose();
        });

        // Cancel
        cancelButton.addActionListener(e -> dispose());

        // Enter key
        priceField.addActionListener(e -> updateButton.doClick());

        // Build form
        formPanel.add(titleLabel);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(stockLabel);

        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(currentPriceLabel);

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(priceLabel);

        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(priceField);

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(updateButton);

        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(cancelButton);

        panel.add(formPanel);
        add(panel);
    }
    /**
     * Validates the new stock price entered by the administrator.
     *
     * Called whenever the content of the price field changes.
     *
     * The method converts the input to a numeric value and
     * delegates price validation to Stock.validatePrice().
     *
     * If the input is valid or the field is empty, the current
     * error message is cleared.
     *
     * If the input is not numeric or does not satisfy the
     * stock price requirements, an error message is displayed.
     */
    private void validatePrice() {
        String input = priceField.getText().trim();
        if (input.isEmpty()) {
            clearError();
            return;
        }
        try {
            double price = Double.parseDouble(input);
            Stock.validatePrice(price);
            clearError();
        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid numeric price.");
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
    /**
     * Clears the currently displayed price validation error.
     */
    private void clearError() {
        errorLabel.setText(" ");
    }
}
