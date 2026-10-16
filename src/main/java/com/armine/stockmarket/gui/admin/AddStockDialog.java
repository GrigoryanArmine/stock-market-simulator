package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.service.MarketService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class AddStockDialog extends JDialog {
    private MarketService marketService;
    private JTextField symbolField;
    private JTextField companyField;
    private JTextField priceField;
    private JLabel errorLabel;

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
