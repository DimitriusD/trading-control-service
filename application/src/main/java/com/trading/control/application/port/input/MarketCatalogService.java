package com.trading.control.application.port.input;

import com.trading.control.application.domain.model.catalog.Catalog;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.instrument.InstrumentPage;
import com.trading.control.application.domain.model.instrument.InstrumentSearchQuery;

public interface MarketCatalogService {

    Catalog getCatalog();

    InstrumentPage searchInstruments(InstrumentSearchQuery query);

    Instrument getInstrument(String instrumentId);
}
