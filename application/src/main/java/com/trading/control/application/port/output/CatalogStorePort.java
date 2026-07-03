package com.trading.control.application.port.output;

import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.market.ExchangeMarket;

import java.util.List;
import java.util.Optional;

public interface CatalogStorePort {

    List<ExchangeMarket> getMarkets();

    Optional<MarketInstruments> getInstruments(String exchange, String marketType);

    Optional<Instrument> findInstrumentByInstrumentId(String instrumentId);

    List<ChannelCapability> getChannelCapabilities(String exchangeCode, String marketCode);
}
