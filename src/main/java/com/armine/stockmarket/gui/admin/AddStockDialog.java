package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.service.MarketService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
/**
 * Provides a modal dialog for adding new stocks to the market.
 *
 * Allows administrators to enter a stock symbol, company name,
 * and initial share price.
 *
 * The dialog validates the entered price as the user types
 * and displays error messages for invalid input.
 *
 * New stocks are created using the Stock model and added
 * to the market through MarketService.
 *
 * After a stock is successfully added, a callback is invoked
 * to refresh the stock management table.
 */
public class AddStockDialog extends JDialog {
    /** Service used to add stocks to the market. */
    private MarketService marketService;
    /** Input field for the new stock symbol. */
    private JTextField symbolField;
    /** Input field for the company name. */
    private JTextField companyField;
    /** Input field for the initial stock price. */
    private JTextField priceField;
    /** Label displaying validation and saving errors. */
    private JLabel errorLabel;
    /**
     * Creates a modal dialog for adding a stock to the market.
     *
     * Initializes input fields for the stock symbol, company
     * name, and initial price, along with buttons for adding
     * the stock or canceling the operation.
     *
     * The price field is monitored for changes so invalid
     * prices can be reported immediately.
     *
     * When the administrator submits the form, a Stock
     * object is created and passed to MarketService.
     *
     * If the operation succeeds, the onStockAdded callback
     * is executed and the dialog closes.
     *
     * If validation or stock creation fails, the error
     * message is displayed and the dialog remains open.
     *
     * @param parent the parent window for the modal dialog
     * @param marketService the service responsible for
     *        managing available market stocks
     * @param onStockAdded the callback invoked after
     *        successfully adding a stock
     * @throws IllegalArgumentException if marketService
     *         or onStockAdded is null
     */
    public AddStockDialog(JFrame parent, MarketService marketService, Runnable onStockAdded) {
        super(parent, "Add Stock", true);
        if (marketService == null || onStockAdded == null)
            throw new IllegalArgumentException("Add stock dependencies cannot be null.");

        this.marketService = marketService;

        setSize(420, 410);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("ADD STOCK");
        JLabel subtitleLabel = new JLabel("Add a new company to the market");

        JLabel symbolLabel = new JLabel("Stock Symbol");
        symbolField = new JTextField(15);

        JLabel companyLabel = new JLabel("Company Name");
        companyField = new JTextField(15);

        JLabel priceLabel = new JLabel("Initial Price ($)");
        priceField = new JTextField(15);

        symbolField.setMaximumSize(symbolField.getPreferredSize());
        companyField.setMaximumSize(companyField.getPreferredSize());
        priceField.setMaximumSize(priceField.getPreferredSize());

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        JButton addButton = new JButton("Add Stock");
        JButton cancelButton = new JButton("Cancel");

        JComponent[] components = {titleLabel, subtitleLabel, symbolLabel, symbolField, companyLabel, companyField, priceLabel, priceField, errorLabel, addButton, cancelButton};

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

        // Clear previous errors when editing other fields
        DocumentListener clearListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearError();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                clearError();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                clearError();
            }
        };

        symbolField.getDocument().addDocumentListener(clearListener);
        companyField.getDocument().addDocumentListener(clearListener);

        // Add stock
        addButton.addActionListener(e -> {
            String symbol = symbolField.getText().trim();
            String company = companyField.getText().trim();
            String priceText = priceField.getText().trim();

            if (priceText.isEmpty()) {
                errorLabel.setText("Please enter a price.");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                Stock stock = new Stock(symbol, company, price);
                marketService.addStock(stock);
            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter a valid numeric price.");
                return;
            } catch (IllegalArgumentException | IllegalStateException ex) {
                errorLabel.setText(ex.getMessage());
                return;
            }
            onStockAdded.run();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
        priceField.addActionListener(e -> addButton.doClick());

        // Build the form
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(subtitleLabel);

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(symbolLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(symbolField);

        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(companyLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(companyField);

        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(priceLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(priceField);

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(addButton);

        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(cancelButton);

        panel.add(formPanel);
        add(panel);
    }
    /**
     * Validates the initial stock price entered by the administrator.
     *
     * Called whenever the content of the price field changes.
     *
     * The method checks whether the input is a valid numeric
     * value and delegates price validation to Stock.validatePrice().
     *
     * If validation succeeds or the field is empty, the
     * current error message is cleared.
     *
     * If the input is invalid, the corresponding error
     * message is displayed in the dialog.
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
     * Clears the currently displayed validation error message.
     */
    private void clearError() {
        errorLabel.setText(" ");
    }
}
