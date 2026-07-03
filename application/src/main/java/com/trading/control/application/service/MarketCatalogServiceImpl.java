package com.trading.control.application.service;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.market.ExchangeMarket;
import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.application.port.output.CatalogStorePort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MarketCatalogServiceImpl implements MarketCatalogService {

    private final CatalogStorePort catalogStore;

    @Override
    public List<ExchangeMarket> getMarkets() {
        return catalogStore.getMarkets();
    }

    @Override
    public MarketInstruments getInstruments(String exchange, String marketType) {
        return catalogStore.getInstruments(exchange, marketType)
                .orElseThrow(() -> new NotFoundException(
                        "Market not found: " + marketType + " for exchange: " + exchange));
    }

    @Override
    public Instrument getInstrument(String instrumentId) {
        if (instrumentId == null || instrumentId.isBlank()) {
            throw new ValidationException("instrumentId must not be blank");
        }
        return catalogStore.findInstrumentByInstrumentId(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found: " + instrumentId));
    }

    @Override
    public List<ChannelCapability> getChannelCapabilities(String exchangeCode, String marketCode) {
        return catalogStore.getChannelCapabilities(exchangeCode, marketCode);
    }
}
