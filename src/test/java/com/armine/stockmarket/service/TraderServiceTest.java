package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Trader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraderServiceTest {

    @Test
    void shouldCreateTraderWithPassword() {
        TraderService service = new TraderService();
        Trader trader = service.createTrader("Anna", "Smith", "anna123", "Password123!".toCharArray());

        assertNotNull(trader);
        assertEquals("anna123", trader.getUsername());
        assertNotEquals("Password123!", trader.getPasswordHash());
    }

    @Test
    void shouldLoginWithCorrectPassword() {
        TraderService service = new TraderService();

        Trader trader = service.createTrader(
                "Anna", "Smith", "anna123",
                "Password123!".toCharArray()
        );

        Trader loggedIn = service.login(
                "anna123", "Password123!".toCharArray()
        );

        assertSame(trader, loggedIn);
    }

    @Test
    void shouldRejectIncorrectPassword() {
        TraderService service = new TraderService();

        service.createTrader(
                "Anna", "Smith", "anna123",
                "Password123!".toCharArray()
        );

        assertThrows(IllegalArgumentException.class, () -> service.login("anna123", "WrongPassword!".toCharArray()));
    }

    @Test
    void shouldRejectShortPassword() {
        TraderService service = new TraderService();

        assertThrows(IllegalArgumentException.class, () ->
                service.createTrader(
                        "Anna", "Smith", "anna123",
                        "123".toCharArray()
                )
        );
    }

    @Test
    void shouldRejectDuplicateUsername() {
        TraderService service = new TraderService();

        service.createTrader(
                "Anna", "Smith", "anna123",
                "Password123!".toCharArray()
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.createTrader(
                        "Bob", "Jones", "anna123",
                        "AnotherPass123!".toCharArray()
                )
        );
    }
}
