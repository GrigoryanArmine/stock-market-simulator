# Stock Market Trading Simulator

A Java-based stock market trading simulator built using object-oriented programming principles, Java Swing, Maven, and JUnit 5.

The application provides graphical and console interfaces for managing simulated stock trading accounts. Traders can buy and sell stocks, manage portfolios, review transaction history, and restore their account data after restarting the application.

The project was developed to practice Java software development, object-oriented design, authentication, exception handling, file persistence, and automated testing.

## Features

### Trader Features
- Register and log in to a trading account
- View available stocks and current market prices
- Buy and sell stocks using simulated funds
- Add money to a trading account
- View account balance and portfolio holdings
- Calculate portfolio value using current market prices
- Review historical BUY and SELL transactions
- Save and restore account information between sessions

### Administrator Features
- Log in through the administrator interface
- Add stocks to the market
- Update stock prices
- Remove stocks when no trader owns shares
- View registered traders and their portfolios

### Additional Functionality
- Java Swing graphical interface
- Interactive console interface
- Input validation and error handling
- Password hashing for authentication
- File-based data persistence
- Defensive copying to protect internal state
- JUnit 5 automated tests

## Technologies

- Java 17
- Java Swing
- Maven
- JUnit 5
- Git and GitHub
- IntelliJ IDEA

## Project Architecture

The application separates responsibilities into four main areas:

**Model Layer:** Defines the main objects, including `User`, `Trader`, `Admin`, `Stock`, `Portfolio`, and `Transaction`.

**Service Layer:** Implements trading operations, market management, user authentication, and application rules.

**Persistence Layer:** Saves and loads market information, trader accounts, portfolio holdings, and transaction history using text files.

**User Interface Layer:** Contains the Java Swing GUI and console interface.

This separation allows business rules to remain independent of the graphical interface.

## Object-Oriented Programming

The project demonstrates:

- **Encapsulation:** Account balances, holdings, and other internal data are managed through class methods.
- **Inheritance:** `Trader` and `Admin` extend `User`.
- **Polymorphism:** Different user types share a common superclass.
- **Composition:** A trader maintains a portfolio and a collection of transactions.
- **Abstraction:** Service classes separate business operations from user-interface code.
- **Exception Handling:** Invalid transactions and operations are handled through validation and exceptions.

## Data Persistence

The simulator stores application information in local text files.

- Market data is stored separately from trader account data.
- Individual traders have their own saved account information.
- Transaction histories are saved separately.
- Historical transactions retain their execution prices.

The persistence services use temporary files and atomic file replacement to reduce the risk of losing an existing file during failed saves. Market operations also include rollback logic for selected saving failures.

**Limitation:** Trader account files and transaction-history files are saved independently. An interruption between these operations can leave their saved states inconsistent. The simulator is an educational application and is not intended for real financial transactions.

## Testing

The project uses JUnit 5 for automated testing.

The tests cover stock validation, portfolio operations, trading operations, authentication, market management, persistence, and selected failure scenarios.

**Latest verified results:**

| Test category | Result |
|---|---|
| Automated JUnit tests | 59 passed |
| Manual GUI checks | 31 passed |
| Final GUI smoke checks | 4 passed |
| Maven clean build | Successful |

Run the automated tests through IntelliJ IDEA's Maven tool window using **Lifecycle → test**, or with an installed Maven command-line executable.

## How to Run

### Requirements
- JDK 17 or later
- IntelliJ IDEA or another Java IDE
- Maven support

### Graphical Interface

1. Clone the repository and open the project in IntelliJ IDEA.
2. Load the Maven project.
3. Select a compatible JDK.
4. Open `src/main/java/com/armine/stockmarket/gui/GuiMain.java`.
5. Run `GuiMain`.
6. Select the trader or administrator interface.

### Console Interface

Run `src/main/java/com/armine/stockmarket/Main.java` in IntelliJ IDEA.

### Running Tests

Open the Maven tool window and execute **Lifecycle → test**.

The test suite uses temporary storage locations for persistence tests, avoiding modifications to normal application data.

## Future Improvements

Potential future extensions include:

- More advanced portfolio performance analytics
- Historical stock price charts
- Improved transaction consistency across multiple files
- Database-backed persistence
- Additional automated integration tests

## Author

Armine Grigoryan

BSc Computer Science student at the American University of Armenia.

This project was developed to strengthen practical Java, object-oriented programming, software design, and testing skills.
