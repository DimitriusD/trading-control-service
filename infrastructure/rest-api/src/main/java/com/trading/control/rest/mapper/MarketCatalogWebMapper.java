package com.trading.control.rest.mapper;

import com.trading.control.application.domain.model.*;
import com.trading.control.restapi.generated.model.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarketCatalogWebMapper {

    MarketCatalogResponseWebDto toMarketCatalogResponse(MarketCatalog catalog);

    MarketInstrumentsResponseWebDto toMarketInstrumentsResponse(MarketInstruments instruments);

    ExchangeWebDto toExchange(ExchangeMarket exchange);

    MarketTypeWebDto toMarketType(MarketType marketType);

    ChannelWebDto toChannel(Channel channel);

    ChannelParamWebDto toChannelParam(ChannelParam param);

    ChannelParamValueWebDto toChannelParamValue(ChannelParamValue value);

    InstrumentWebDto toInstrument(Instrument instrument);

    AssetWebDto toAsset(Asset asset);
}
