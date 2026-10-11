
package com.armine.stockmarket.gui.admin;

import com.armine.stockmarket.model.Stock;
import com.armine.stockmarket.service.MarketService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Locale;

public class UpdateStockPriceDialog extends JDialog {

    private final MarketService marketService;
    private final Stock stock;
    private final JTextField priceField;
    private final JLabel errorLabel;

    public UpdateStockPriceDialog(
            JFrame parent,
            MarketService marketService,
            Stock stock,
            Runnable onPriceUpdated) {

        super(parent, "Update Stock Price", true);

        this.marketService = marketService;
        this.stock = stock;

        setSize(400, 340);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("UPDATE STOCK PRICE");

        JLabel stockLabel = new JLabel(
                stock.getSymbol() + " - " + stock.getCompanyName()
        );

        JLabel currentPriceLabel = new JLabel(
                "Current price: $" +
                        String.format(Locale.US, "%.2f", stock.getCurrentPrice())
        );

        JLabel priceLabel = new JLabel("New Price ($)");

        priceField = new JTextField(15);
        priceField.setMaximumSize(priceField.getPreferredSize());

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);

        JButton updateButton = new JButton("Update Price");
        JButton cancelButton = new JButton("Cancel");

        JComponent[] components = {
                titleLabel, stockLabel, currentPriceLabel,
                priceLabel, priceField, errorLabel,
                updateButton, cancelButton
        };

        for (JComponent component : components) {
            component.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        priceField.getDocument().addDocumentListener(new DocumentListener() {
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
        });

        updateButton.addActionListener(e -> {

            String input = priceField.getText().trim();

            if (input.isEmpty()) {
                errorLabel.setText("Please enter a price.");
                return;
            }

            if (!validatePrice()) {
                return;
            }

            try {
                double newPrice = Double.parseDouble(input);

                marketService.updatePrice(stock.getSymbol(), newPrice);

                onPriceUpdated.run();
                dispose();

            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dispose());
        priceField.addActionListener(e -> updateButton.doClick());

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
    private boolean validatePrice() {
        String input = priceField.getText().trim();
        if (input.isEmpty()) {
            errorLabel.setText(" ");
            return false;
        }
        try {
            double price = Double.parseDouble(input);
            Stock.validatePrice(price);
            errorLabel.setText(" ");
            return true;
        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid numeric price.");
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
        }
        return false;
    }
}
