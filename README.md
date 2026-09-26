# Stock Market Trading Simulator

A Java-based stock market trading simulator built to practice object-oriented programming, software design, unit testing, exception handling, and file-based data persistence.

The application allows a trader to buy and sell stocks, manage a portfolio, track transactions, and continue from previously saved data after restarting the application.

## Features

* View available stocks
* Buy stocks
* Sell stocks
* View portfolio
* Track account balance
* Add money to the account
* View transaction history
* Input validation
* Custom exceptions for trading errors
* Save and load trader data
* Save and load transaction history
* Unit testing with JUnit 5
* Console-based user interface

## Technologies

* **Java 17**
* **Maven**
* **JUnit 5**
* **IntelliJ IDEA**
* **Git & GitHub**

## Architecture

The project separates the user interface, business logic, data models, and persistence.

```text
                         Main
                          |
                    ConsoleUI
                          |
              +-----------+-----------+
              |                       |
       TradingService           Persistence
              |                 Services
              |                       |
            Trader              Text Files
              |
       +------+------+
       |             |
   Portfolio    Transactions
```

## Main Components

### Model

Contains the core objects of the application:

* `User`
* `Trader`
* `Admin`
* `Stock`
* `Portfolio`
* `Transaction`
* `TransactionType`

### Services

Contains the application's business logic and persistence operations:

* `MarketService` — manages stocks available on the market.
* `TradingService` — handles buying and selling stocks.
* `TransactionService` — saves and loads transaction history.
* `TraderPersistenceService` — saves and loads trader information and portfolio data.

### UI

* `ConsoleUI` — provides the interactive command-line interface and handles user input.

### Exceptions

* `InsufficientFundsException`
* `InsufficientSharesException`

These exceptions are used to handle specific trading errors.

## Object-Oriented Programming Concepts

This project demonstrates several core OOP concepts.

### Encapsulation

Class fields are kept private and accessed through methods.

For example, `Trader` manages its balance, portfolio, and transactions instead of exposing its internal fields directly.

### Inheritance

`Trader` and `Admin` extend the `User` class.

```text
        User
       /    \
   Trader   Admin
```

This allows common user information, such as the name, to be defined once in the parent class.

### Polymorphism

Objects of different subclasses can be referenced using their parent type.

```java
User user1 = trader;
User user2 = admin;
```

The objects retain their specific behavior while being referenced through the common `User` type.

### Composition

A `Trader` has a `Portfolio` and a collection of `Transaction` objects.

This represents a "has-a" relationship between the classes.

### Abstraction and Separation of Responsibilities

Different parts of the application have different responsibilities.

For example:

```text
ConsoleUI
    ↓
TradingService
    ↓
Trader / Portfolio
```

The UI handles interaction with the user, while the service layer handles trading operations.

This separation makes the application easier to maintain and extend.

### Enums

`TransactionType` represents the two supported transaction types:

```java
BUY
SELL
```

Using an enum prevents transaction types from being represented by arbitrary strings throughout the application.

### Custom Exceptions

The application uses custom checked exceptions for specific trading errors:

* `InsufficientFundsException`
* `InsufficientSharesException`

This makes trading failures more explicit and easier to handle.

## Persistence

The application uses simple text files to preserve data between sessions.

### Trader Data

Trader information and portfolio holdings are stored in:

```text
trader.txt
```

The stored information includes:

* Trader name
* Account balance
* Stock symbol
* Company name
* Current price
* Number of shares

### Transaction History

Transaction history is stored in:

```text
transactions.txt
```

Each transaction contains:

* Transaction type
* Stock symbol
* Company name
* Quantity
* Price per share
* Date and time

When the application starts, previously saved trader and transaction data is loaded automatically.

When the user exits the application, the current state is saved.

This allows the application to continue from the previous session instead of starting from an empty account every time.

## Testing

The project uses **JUnit 5** for automated testing.

The test suite covers:

* Stock creation and validation
* Stock symbol normalization
* Stock equality
* Stock price updates
* Portfolio operations
* Adding and removing shares
* Portfolio value calculation
* Buying stocks
* Selling stocks
* Insufficient funds
* Insufficient shares
* Invalid quantities
* Transaction creation
* Transaction validation
* Transaction date/time
* Transaction persistence
* Trader persistence

### Current Test Result

**27 tests passing**

The tests are organized according to the application's packages:

```text
src/test/java/
└── com/armine/stockmarket/
    ├── model/
    │   ├── StockTest.java
    │   ├── PortfolioTest.java
    │   └── TransactionTest.java
    │
    └── service/
        ├── TradingServiceTest.java
        ├── TransactionServiceTest.java
        └── TraderPersistenceServiceTest.java
```

## Example

When the application starts, the user is presented with the main menu:

```text
==============================
   STOCK MARKET SIMULATOR
==============================
Trader: Armine
Balance: $9338.5

1. View available stocks
2. Buy stock
3. Sell stock
4. View portfolio
5. View transactions
6. Add money
7. Exit

Choose an option:
```

### Available Stocks

The simulator starts with several stocks available in the market:

```text
AAPL - Apple ($220.5)
MSFT - Microsoft ($510.0)
GOOG - Google ($250.0)
AMZN - Amazon ($230.0)
TSLA - Tesla ($350.0)
```

### Portfolio

After purchasing stocks, the portfolio can display the current holdings and total value:

```text
My Portfolio:
AAPL - 3 shares
Total value: $661.5
```

### Transactions

The application records every successful buy and sell operation:

```text
Transactions:
BUY 3 shares of AAPL at $220.5 each | Total: $661.5 | 2026-
```
