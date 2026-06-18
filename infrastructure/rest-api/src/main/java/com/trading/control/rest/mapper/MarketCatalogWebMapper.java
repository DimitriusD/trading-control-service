package com.trading.control.rest.mapper;

import com.trading.control.application.domain.model.*;
import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.restapi.generated.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MarketCatalogWebMapper {

    MarketCatalogResponseWebDto toMarketCatalogResponse(MarketCatalog catalog);

    MarketInstrumentsResponseWebDto toMarketInstrumentsResponse(MarketInstruments instruments);

    ExchangeWebDto toExchange(ExchangeMarket exchange);

    MarketTypeWebDto toMarketType(MarketType marketType);

    ChannelWebDto toChannel(Channel channel);

    ChannelParamWebDto toChannelParam(ChannelParam param);

    @Mapping(target = "isDefault", source = "default")
    ChannelParamValueWebDto toChannelParamValue(ChannelParamValue value);

    InstrumentWebDto toInstrument(Instrument instrument);

    AssetWebDto toAsset(Asset asset);
}
