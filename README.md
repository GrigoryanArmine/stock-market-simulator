# Stock Market Trading Simulator



## Features


## Technologies


## Architecture


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


### Services

Contains the application's business logic and persistence operations:


### UI


### Exceptions


These exceptions are used to handle specific trading errors.

## Object-Oriented Programming Concepts

This project demonstrates several core OOP concepts.

### Encapsulation


### Inheritance

`Trader` and `Admin` extend the `User` class.

``` text
        User
       /    \
   Trader   Admin
```


### Polymorphism


``` java
User user1 = trader;
User user2 = admin;
```


### Composition

A `Trader` has a `Portfolio` and a collection of `Transaction` objects.
This represents a "has-a" relationship between the classes.


Different parts of the application have different responsibilities.

``` text
ConsoleUI
TradingService
Trader / Portfolio
```


### Enums


``` java
BUY
SELL
```


### Custom Exceptions



This makes trading failures more explicit and easier to handle.

## Persistence


### Trader Data


The stored information includes:


### Transaction History


Each transaction contains:



## Testing

The project uses **JUnit 5** for automated testing.

The test suite covers:





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


``` text
AAPL - Apple ($220.5)
MSFT - Microsoft ($510.0)
GOOG - Google ($250.0)
AMZN - Amazon ($230.0)
TSLA - Tesla ($350.0)
```

### Portfolio


``` text
My Portfolio:
AAPL - 3 shares
Total value: $661.5
```

### Transactions


``` text
```
