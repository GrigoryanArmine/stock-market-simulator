# Stock Market Trading Simulator

A Java-based stock market trading simulator built to practice
object-oriented programming, software design, unit testing, exception
handling, and file-based data persistence.

The application allows a trader to buy and sell stocks, manage a
portfolio, track transactions, and continue from previously saved data
after restarting the application.

## Features

-   View available stocks
-   Buy stocks
-   Sell stocks
-   View portfolio
-   Track account balance
-   Add money to the account
-   View transaction history
-   Input validation
-   Custom exceptions for trading errors
-   Save and load trader data
-   Save and load transaction history
-   Unit testing with JUnit 5
-   Console-based user interface

## Technologies

-   **Java 17**
-   **Maven**
-   **JUnit 5**
-   **IntelliJ IDEA**
-   **Git & GitHub**

## Architecture

The project separates the user interface, business logic, data models,
and persistence.

``` text
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

-   `User`
-   `Trader`
-   `Admin`
-   `Stock`
-   `Portfolio`
-   `Transaction`
-   `TransactionType`

### Services

Contains the application's business logic and persistence operations:

-   `MarketService` --- manages stocks available on the market.
-   `TradingService` --- handles buying and selling stocks.
-   `TransactionService` --- saves and loads transaction history.
-   `TraderPersistenceService` --- saves and loads trader information
    and portfolio data.

### UI

-   `ConsoleUI` --- provides the interactive command-line interface and
    handles user input.

### Exceptions

-   `InsufficientFundsException`
-   `InsufficientSharesException`

These exceptions are used to handle specific trading errors.

## Object-Oriented Programming Concepts

This project demonstrates several core OOP concepts.

### Encapsulation

Class fields are kept private and accessed through methods. For example,
`Trader` manages its balance, portfolio, and transactions through its
methods.

### Inheritance

`Trader` and `Admin` extend the `User` class.

``` text
        User
       /    \
   Trader   Admin
```

This allows common user information, such as the name, to be defined
once in the parent class.

### Polymorphism

Objects of different subclasses can be referenced using their parent
type.

``` java
User user1 = trader;
User user2 = admin;
```

The objects can be handled through the common `User` type while
retaining their actual subclass identity.

### Composition

A `Trader` has a `Portfolio` and a collection of `Transaction` objects.
This represents a "has-a" relationship between the classes.

### Separation of Responsibilities

Different parts of the application have different responsibilities.

``` text
ConsoleUI
    |
TradingService
    |
Trader / Portfolio
```

The UI handles interaction with the user, while the service layer
handles trading operations. This separation makes the application easier
to maintain and extend.

### Enums

`TransactionType` represents the supported transaction types:

``` java
BUY
SELL
```

Using an enum avoids representing transaction types as arbitrary strings
throughout the application.

### Custom Exceptions

The application uses custom checked exceptions for specific trading
errors:

-   `InsufficientFundsException`
-   `InsufficientSharesException`

This makes trading failures more explicit and easier to handle.

## Persistence

The application uses text files to preserve data between sessions.

### Trader Data

Trader information and portfolio holdings are stored in `trader.txt`.

The stored information includes:

-   Trader name
-   Account balance
-   Stock symbol
-   Company name
-   Current price
-   Number of shares

### Transaction History

Transaction history is stored in `transactions.txt`.

Each transaction contains:

-   Transaction type
-   Stock symbol
-   Company name
-   Quantity
-   Price per share
-   Date and time

When the application starts, previously saved trader and transaction
data is loaded. When the user exits, the current state is saved.

## Testing

The project uses **JUnit 5** for automated testing.

The test suite covers:

-   Stock creation and validation
-   Stock symbol normalization and equality
-   Stock price updates
-   Portfolio operations, including adding and removing shares
-   Portfolio value calculation
-   Buying and selling stocks
-   Insufficient funds and insufficient shares
-   Invalid quantities
-   Transaction creation, validation, and date/time
-   Transaction persistence
-   Trader persistence

### Test Results

**27 tests passed** in the latest verified run.

The tests are organized by package:

``` text
src/test/java/
└── com/armine/stockmarket/
    ├── model/
    │   ├── StockTest.java
    │   ├── PortfolioTest.java
    │   └── TransactionTest.java
    └── service/
        ├── TradingServiceTest.java
        ├── TransactionServiceTest.java
        └── TraderPersistenceServiceTest.java
```

## Example

The application presents the trader with a console menu similar to the
following:

``` text
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

The market is initialized with example stocks:

``` text
AAPL - Apple ($220.5)
MSFT - Microsoft ($510.0)
GOOG - Google ($250.0)
AMZN - Amazon ($230.0)
TSLA - Tesla ($350.0)
```

### Portfolio

After purchasing stocks, the portfolio displays the trader's holdings
and their current total value:

``` text
My Portfolio:
AAPL - 3 shares
Total value: $661.5
```

### Transactions

Successful buy and sell operations are recorded in the transaction
history. An example entry is:

``` text
BUY 3 shares of AAPL at $220.5 each | Total: $661.5 | 2026-09-26T23:13:54
```

## Project Structure

``` text
stock-market-simulator/
├── README.md
├── .gitignore
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/armine/stockmarket/
    │           ├── Main.java
    │           ├── model/
    │           │   ├── Admin.java
    │           │   ├── Portfolio.java
    │           │   ├── Stock.java
    │           │   ├── Trader.java
    │           │   ├── Transaction.java
    │           │   ├── TransactionType.java
    │           │   └── User.java
    │           ├── service/
    │           │   ├── MarketService.java
    │           │   ├── TradingService.java
    │           │   ├── TransactionService.java
    │           │   └── TraderPersistenceService.java
    │           ├── exception/
    │           │   ├── InsufficientFundsException.java
    │           │   └── InsufficientSharesException.java
    │           └── ui/
    │               └── ConsoleUI.java
    └── test/
        └── java/
            └── com/armine/stockmarket/
                ├── model/
                │   ├── StockTest.java
                │   ├── PortfolioTest.java
                │   └── TransactionTest.java
                └── service/
                    ├── TradingServiceTest.java
                    ├── TransactionServiceTest.java
                    └── TraderPersistenceServiceTest.java
```

## How to Run

### Requirements

-   Java 17 or later
-   IntelliJ IDEA
-   Maven support

### Run the Application in IntelliJ IDEA

1.  Clone or download the repository.
2.  Open the project in IntelliJ IDEA.
3.  Configure a compatible JDK (Java 17 or later).
4.  Reload the Maven project if prompted.
5.  Open `src/main/java/com/armine/stockmarket/Main.java`.
6.  Run the `Main` class.

The application saves its data to text files when the trader exits.

### Run the Tests

In IntelliJ IDEA, open the test source directory under `src/test/java`
and run the test classes or the test package.

## Data Persistence

The application saves trader and transaction information when the user
exits. On the next launch, it loads the saved information so the session
can continue.

For example:

``` text
Before exiting:
Balance: $9338.5
Portfolio: AAPL - 3 shares

        |
        v

Application exits and saves data

        |
        v

Application starts again and loads saved data

        |
        v

Balance: $9338.5
Portfolio: AAPL - 3 shares
```

## Future Improvements

Possible future improvements include:

-   A graphical user interface using Java Swing
-   More advanced market price simulation
-   Price updates during the simulation
-   Multiple trader accounts
-   Portfolio performance statistics
-   An improved persistence format
-   Additional automated tests
-   More detailed transaction and portfolio analytics

## Learning Goals

This project was developed as a practical way to strengthen Java and
object-oriented programming skills.

The main goals were to practice:

-   Designing classes and relationships
-   Applying OOP principles
-   Encapsulation, inheritance, polymorphism, and composition
-   Separating responsibilities between classes
-   Handling exceptions
-   Writing unit tests
-   Working with files
-   Designing service classes
-   Using Git and GitHub
-   Building a complete application from the model layer to the user
    interface

## Author

**Armine Grigoryan**

Computer Science student interested in Java, object-oriented
programming, and software development.
