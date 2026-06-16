package com.trading.control.application.port.output;

import com.trading.control.application.domain.model.catalog.Exchange;
import com.trading.control.application.domain.model.catalog.ExchangeMarketLink;
import com.trading.control.application.domain.model.catalog.MarketType;

import java.util.List;

/**
 * Output port for reading the market catalog from durable storage.
 * Implemented by the jdbc-storage-adapter module.
 */
public interface CatalogStorePort {

    List<Exchange> findExchanges();

    List<MarketType> findMarketTypes();

    List<ExchangeMarketLink> findExchangeMarkets();
}
