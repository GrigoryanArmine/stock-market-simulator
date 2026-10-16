package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.service.MarketService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;

public class UpdateStockPriceDialog extends JDialog {
    private MarketService marketService;
    private Stock stock;
    private JTextField priceField;
    private JLabel errorLabel;

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

    private void clearError() {
        errorLabel.setText(" ");
    }
}
