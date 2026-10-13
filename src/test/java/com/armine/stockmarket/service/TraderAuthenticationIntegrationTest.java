package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Trader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraderAuthenticationIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldRegisterSaveLoadAndLoginTrader() {

        // 1. Register a new Trader
        TraderService registrationService = new TraderService();

        Trader originalTrader = registrationService.createTrader(
                "Armine",
                "Grigoryan",
                "armine123",
                "SecurePass123!".toCharArray()
        );

        assertNotNull(originalTrader);
        assertNotNull(originalTrader.getPasswordHash());

        // Password must not be stored as plaintext
        assertNotEquals(
                "SecurePass123!",
                originalTrader.getPasswordHash()
        );

        // 2. Save the Trader
        TraderPersistenceService persistenceService =
                new TraderPersistenceService(tempDir.toString());

        persistenceService.saveTrader(originalTrader);

        // 3. Simulate restarting the application
        TraderService restartedService = new TraderService();

        MarketService marketService = new MarketService();
        marketService.initializeMarket();

        List<Trader> loadedTraders =
                persistenceService.loadAllTraders(marketService);

        assertEquals(1, loadedTraders.size());

        for (Trader trader : loadedTraders) {
            restartedService.addTrader(trader);
        }

        // 4. Login with the correct password
        Trader loggedInTrader = restartedService.login(
                "armine123",
                "SecurePass123!".toCharArray()
        );

        assertNotNull(loggedInTrader);
        assertEquals("armine123", loggedInTrader.getUsername());
        assertEquals(originalTrader.getId(), loggedInTrader.getId());

        // 5. Login with the wrong password must fail
        assertThrows(
                IllegalArgumentException.class,
                () -> restartedService.login(
                        "armine123",
                        "WrongPassword123!".toCharArray()
                )
        );

        // 6. Unknown username must also fail
        assertThrows(
                IllegalArgumentException.class,
                () -> restartedService.login(
                        "unknown_user",
                        "SecurePass123!".toCharArray()
                )
        );
    }
}
