package com.trading.control.application.service;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.model.MarketCatalog;
import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.application.port.output.CatalogStorePort;

public class MarketCatalogServiceImpl implements MarketCatalogService {

    private final CatalogStorePort catalogStore;

    public MarketCatalogServiceImpl(CatalogStorePort catalogStore) {
        this.catalogStore = catalogStore;
    }

    @Override
    public MarketCatalog getMarkets() {
        return catalogStore.getMarkets();
    }

    @Override
    public MarketInstruments getInstruments(String exchange, String marketType) {
        return catalogStore.getInstruments(exchange, marketType)
                .orElseThrow(() -> new NotFoundException(
                        "Market not found: " + marketType + " for exchange: " + exchange));
    }
}
