
package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.service.MarketService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class AddStockDialog extends JDialog {

    private final MarketService marketService;
    private final JTextField symbolField;
    private final JTextField companyField;
    private final JTextField priceField;
    private final JLabel errorLabel;

    public AddStockDialog(
            JFrame parent,
            MarketService marketService,
            Runnable onStockAdded) {

        super(parent, "Add Stock", true);

        this.marketService = marketService;

        setSize(420, 410);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("ADD STOCK");
        JLabel subtitleLabel =
                new JLabel("Add a new company to the market");

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

        JComponent[] components = {
                titleLabel, subtitleLabel,
                symbolLabel, symbolField,
                companyLabel, companyField,
                priceLabel, priceField,
                errorLabel, addButton, cancelButton
        };

        for (JComponent component : components) {
            component.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        // Real-time validation
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }
        };

        symbolField.getDocument().addDocumentListener(listener);
        companyField.getDocument().addDocumentListener(listener);
        priceField.getDocument().addDocumentListener(listener);

        addButton.addActionListener(e -> {

            String symbol = symbolField.getText().trim().toUpperCase();
            String company = companyField.getText().trim();
            String priceText = priceField.getText().trim();

            if (symbol.isEmpty() || company.isEmpty() || priceText.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                return;
            }

            if (!validateInput()) {
                return;
            }

            try {
                double price = Double.parseDouble(priceText);

                marketService.addStock(
                        new Stock(symbol, company, price)
                );

                onStockAdded.run();
                dispose();

            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dispose());
        priceField.addActionListener(e -> addButton.doClick());

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


    private boolean validateInput() {
        String symbol = symbolField.getText().trim().toUpperCase();
        String company = companyField.getText().trim();
        String priceText = priceField.getText().trim();

        // Check duplicate stock symbol
        if (!symbol.isEmpty() && marketService.findStock(symbol) != null) {
            errorLabel.setText("Stock symbol already exists.");
            return false;
        }

        // Validate price using Stock.java
        if (!priceText.isEmpty()) {
            try {
                double price = Double.parseDouble(priceText);
                Stock.validatePrice(price);

            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter a valid numeric price.");
                return false;
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                return false;
            }
        }

        errorLabel.setText(" ");
        return !symbol.isEmpty() && !company.isEmpty() && !priceText.isEmpty();
    }

}
