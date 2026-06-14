package com.trading.control.application.port.input;

import com.trading.control.application.domain.model.MarketCatalog;
import com.trading.control.application.domain.model.MarketInstruments;

public interface MarketCatalogService {

    MarketCatalog getMarkets();

    MarketInstruments getInstruments(String exchange, String marketType);
}
