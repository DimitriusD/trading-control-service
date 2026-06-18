package com.trading.control.application.port.output;

import com.trading.control.application.domain.model.MarketCatalog;
import com.trading.control.application.domain.model.MarketInstruments;

import java.util.Optional;

/**
 * Output port for reading the market catalog from durable storage.
 * Implemented by the jdbc-storage-adapter module.
 */
public interface CatalogStorePort {

    /**
     * The full market catalog: exchanges → market types → channels → params → allowed values.
     */
    MarketCatalog getMarkets();

    /**
     * Instruments offered for a given exchange/market-type pair.
     *
     * @return empty if the exchange/market-type pair does not exist; otherwise the
     * (possibly empty) instrument list.
     */
    Optional<MarketInstruments> getInstruments(String exchange, String marketType);
}
