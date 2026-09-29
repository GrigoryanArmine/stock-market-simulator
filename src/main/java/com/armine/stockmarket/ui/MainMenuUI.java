package com.armine.stockmarket.ui;

import com.armine.stockmarket.model.Trader;
import com.armine.stockmarket.service.MarketService;
import com.armine.stockmarket.service.TraderPersistenceService;
import com.armine.stockmarket.service.TraderService;
import com.armine.stockmarket.service.TradingService;
import com.armine.stockmarket.service.TransactionService;

import java.util.List;
import java.util.Scanner;

public class MainMenuUI {
    private TraderService traderService;
    private TraderPersistenceService traderPersistenceService;
    private MarketService marketService;
    private TradingService tradingService;
    private TransactionService transactionService;
    private Scanner scanner;

    public MainMenuUI(
            TraderService traderService,
            TraderPersistenceService traderPersistenceService,
            MarketService marketService,
            TradingService tradingService,
            TransactionService transactionService) {
        this.traderService = traderService;
        this.traderPersistenceService = traderPersistenceService;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        System.out.println();
        System.out.println("==============================");
        System.out.println("   STOCK MARKET SIMULATOR");
        System.out.println("==============================");
        System.out.println();
        System.out.println("1. Select trader");
        System.out.println("2. Create new trader");
        System.out.println("3. Admin");
        System.out.println("4. Exit");
        System.out.println();
        System.out.print("Choose an option: ");
    }
    private void createTrader() {
        System.out.println();
        System.out.print("Enter trader name: ");
        String name = scanner.nextLine().trim();

        if (name.isBlank()) {
            System.out.println("Trader name cannot be empty.");
            return;
        }
        if (traderService.findTrader(name) != null) {
            System.out.println("Trader already exists.");
            return;
        }

        Trader trader = new Trader(name, 10000);
        traderService.addTrader(trader);
        traderPersistenceService.saveTrader(trader);
        System.out.println("Trader " + name + " created successfully.");
    }
    public void start() {
        boolean running = true;
        while (running) {
            showMenu();
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    Trader selectedTrader = selectTrader();
                    if (selectedTrader != null) {
                        transactionService.loadTransactions(selectedTrader, marketService);
                        ConsoleUI consoleUI = new ConsoleUI(
                                        selectedTrader,
                                        marketService,
                                        tradingService,
                                        traderPersistenceService,
                                        transactionService);
                        consoleUI.start();
                    }
                    break;
                case "2":
                    createTrader();
                    break;
                case "3":
                    System.out.println("Admin - coming later.");
                    break;
                case "4":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    private Trader selectTrader() {
        List<Trader> traders = traderService.getAllTraders();

        if (traders.isEmpty()) {
            System.out.println();
            System.out.println("No traders available.");
            System.out.println("Create a trader first.");
            return null;
        }
        System.out.println();
        System.out.println("Available traders:");

        for (int i = 0; i < traders.size(); i++) {
            System.out.println((i + 1) + ". " + traders.get(i).getName());
        }
        while (true) {
            System.out.println();
            System.out.print("Choose trader number: ");
            String input = scanner.nextLine();

            try {

                int choice = Integer.parseInt(input);
                if (choice < 1 || choice > traders.size()) {
                    System.out.println("Invalid trader number.");
                    continue;
                }
                return traders.get(choice - 1);

            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}