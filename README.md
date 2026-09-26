# Stock Market Trading Simulator

A Java-based stock market trading simulator built to practice object-oriented programming, software design, testing, and file-based data persistence.

## Features

- View available stocks
- Buy stocks
- Sell stocks
- View portfolio
- Track account balance
- Add money to the account
- View transaction history
- Save and load trader data
- Save and load transaction history
- Input validation
- Custom exceptions
- Unit tests with JUnit 5
- Console-based user interface

## Technologies

- Java 17
- Maven
- JUnit 5
- IntelliJ IDEA
- Git & GitHub

## Project Structure

```text
src/
├── main/
│   └── java/
│       └── com.armine.stockmarket/
│           ├── Main.java
│           │
│           ├── model/
│           │   ├── Admin.java
│           │   ├── Portfolio.java
│           │   ├── Stock.java
│           │   ├── Trader.java
│           │   ├── Transaction.java
│           │   ├── TransactionType.java
│           │   └── User.java
│           │
│           ├── service/
│           │   ├── MarketService.java
│           │   ├── TradingService.java
│           │   ├── TransactionService.java
│           │   └── TraderPersistenceService.java
│           │
│           ├── exception/
│           │   ├── InsufficientFundsException.java
│           │   └── InsufficientSharesException.java
│           │
│           └── ui/
│               └── ConsoleUI.java
│
└── test/
    └── java/
        └── com.armine.stockmarket/
            ├── model/
            └── service/
