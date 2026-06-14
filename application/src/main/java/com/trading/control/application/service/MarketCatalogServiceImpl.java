package com.trading.control.application.service;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.model.MarketCatalog;
import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.port.input.MarketCatalogService;

public class MarketCatalogServiceImpl implements MarketCatalogService {

    @Override
    public MarketCatalog getMarkets() {
        return StaticMarketCatalog.getCatalog();
    }

    @Override
    public MarketInstruments getInstruments(String exchange, String marketType) {
        if (!StaticMarketCatalog.hasExchange(exchange)) {
            throw new NotFoundException("Exchange not found: " + exchange);
        }
        if (!StaticMarketCatalog.hasMarketType(exchange, marketType)) {
            throw new NotFoundException("Market type not found: " + marketType + " for exchange: " + exchange);
        }

        return MarketInstruments.builder()
                .exchange(exchange)
                .marketType(marketType)
                .instruments(StaticMarketCatalog.getInstruments(exchange, marketType))
                .build();
    }
}
