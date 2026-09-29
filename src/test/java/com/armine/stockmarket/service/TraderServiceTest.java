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
                new Trader("Armine", 10000);

        traderService.addTrader(trader);

        assertEquals(
                1,
                traderService.getAllTraders().size()
        );
    }

    @Test
    void shouldFindTraderByName() {

        TraderService traderService =
                new TraderService();

        Trader trader =
                new Trader("Armine", 10000);

        traderService.addTrader(trader);

        Trader result =
                traderService.findTrader("Armine");

        assertSame(trader, result);
    }

    @Test
    void shouldFindTraderIgnoringCase() {

        TraderService traderService =
                new TraderService();

        Trader trader =
                new Trader("Armine", 10000);

        traderService.addTrader(trader);

        Trader result =
                traderService.findTrader("armine");

        assertSame(trader, result);
    }

    @Test
    void shouldNotAllowDuplicateTraderNames() {

        TraderService traderService =
                new TraderService();

        traderService.addTrader(
                new Trader("Armine", 10000)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> traderService.addTrader(
                        new Trader("Armine", 5000)
                )
        );
    }

    @Test
    void shouldReturnNullWhenTraderDoesNotExist() {

        TraderService traderService =
                new TraderService();

        Trader result =
                traderService.findTrader("David");

        assertNull(result);
    }
}