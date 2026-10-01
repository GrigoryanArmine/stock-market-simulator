package com.armine.stockmarket.service;

import com.armine.stockmarket.model.Trader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraderServiceTest {

    @Test
    void shouldAddTrader() {
        TraderService traderService =
                new TraderService();

        Trader trader =
                new Trader(
                        123456,
                        "armine",
                        "Armine",
                        "Grigoryan",
                        10000
                );

        traderService.addTrader(trader);

        assertEquals(
                1,
                traderService.getAllTraders().size()
        );
    }

    @Test
    void shouldFindTraderByUsername() {
        TraderService traderService =
                new TraderService();

        Trader trader =
                new Trader(
                        123456,
                        "armine",
                        "Armine",
                        "Grigoryan",
                        10000
                );

        traderService.addTrader(trader);

        Trader result =
                traderService.findTraderByUsername("armine");

        assertSame(trader, result);
    }

    @Test
    void shouldFindTraderByUsernameIgnoringCase() {
        TraderService traderService =
                new TraderService();

        Trader trader =
                new Trader(
                        123456,
                        "Armine",
                        "Armine",
                        "Grigoryan",
                        10000
                );

        traderService.addTrader(trader);

        Trader result =
                traderService.findTraderByUsername("armine");

        assertSame(trader, result);
    }

    @Test
    void shouldFindTraderById() {
        TraderService traderService =
                new TraderService();

        Trader trader =
                new Trader(
                        123456,
                        "armine",
                        "Armine",
                        "Grigoryan",
                        10000
                );

        traderService.addTrader(trader);

        Trader result =
                traderService.findTraderById(123456);

        assertSame(trader, result);
    }

    @Test
    void shouldNotAllowDuplicateUsernames() {
        TraderService traderService =
                new TraderService();

        Trader trader1 =
                new Trader(
                        123456,
                        "armine",
                        "Armine",
                        "Grigoryan",
                        10000
                );

        Trader trader2 =
                new Trader(
                        654321,
                        "ARMINE",
                        "Anna",
                        "Smith",
                        5000
                );

        traderService.addTrader(trader1);

        assertThrows(
                IllegalArgumentException.class,
                () -> traderService.addTrader(trader2)
        );
    }

    @Test
    void shouldNotAllowDuplicateIds() {
        TraderService traderService =
                new TraderService();

        Trader trader1 =
                new Trader(
                        123456,
                        "armine",
                        "Armine",
                        "Grigoryan",
                        10000
                );

        Trader trader2 =
                new Trader(
                        123456,
                        "david",
                        "David",
                        "Smith",
                        5000
                );

        traderService.addTrader(trader1);

        assertThrows(
                IllegalArgumentException.class,
                () -> traderService.addTrader(trader2)
        );
    }

    @Test
    void shouldReturnNullWhenUsernameDoesNotExist() {
        TraderService traderService =
                new TraderService();

        Trader result =
                traderService.findTraderByUsername("david");

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenIdDoesNotExist() {
        TraderService traderService =
                new TraderService();

        Trader result =
                traderService.findTraderById(123456);

        assertNull(result);
    }

    @Test
    void shouldGenerateSixDigitId() {
        TraderService traderService =
                new TraderService();

        int id = traderService.generateUniqueId();

        assertTrue(id >= 100000);
        assertTrue(id <= 999999);
    }
}