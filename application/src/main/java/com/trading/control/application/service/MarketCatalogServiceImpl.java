package com.trading.control.application.service;

import com.trading.control.application.domain.model.catalog.Catalog;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.instrument.InstrumentPage;
import com.trading.control.application.domain.model.instrument.InstrumentSearchQuery;
import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.application.port.output.MarketCatalogPort;

public class MarketCatalogServiceImpl implements MarketCatalogService {

    private final MarketCatalogPort marketCatalogPort;

    public MarketCatalogServiceImpl(MarketCatalogPort marketCatalogPort) {
        this.marketCatalogPort = marketCatalogPort;
    }

    @Override
    public Catalog getCatalog() {
        return marketCatalogPort.getCatalog();
    }

    @Override
    public InstrumentPage searchInstruments(InstrumentSearchQuery query) {
        return marketCatalogPort.searchInstruments(query);
    }

    @Override
    public Instrument getInstrument(String instrumentId) {
        return marketCatalogPort.getInstrument(instrumentId);
    }
}
