package com.trading.control.application.port.input;

import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.market.ExchangeMarket;

import java.util.List;

public interface MarketCatalogService {

    List<ExchangeMarket> getMarkets();

    MarketInstruments getInstruments(String exchange, String marketType);

    Instrument getInstrument(String instrumentId);

    List<ChannelCapability> getChannelCapabilities(String exchangeCode, String marketCode);
}
