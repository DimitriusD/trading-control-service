package com.trading.control.application.port.output;

import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.instrument.Instrument;

import java.util.List;

public interface MarketCatalogPort {

    Instrument getInstrument(String instrumentId);

    List<ChannelCapability> getChannelCapabilities(String exchangeCode, String marketCode);
}
