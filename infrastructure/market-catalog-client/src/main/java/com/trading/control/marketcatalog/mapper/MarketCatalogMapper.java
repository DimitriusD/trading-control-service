package com.trading.control.marketcatalog.mapper;

import com.trading.catalog.client.model.AssetDto;
import com.trading.catalog.client.model.ChannelCapabilityDto;
import com.trading.catalog.client.model.ChannelParamCapabilityDto;
import com.trading.catalog.client.model.InstrumentDto;
import com.trading.control.application.domain.model.Asset;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.catalog.ChannelParamCapability;
import com.trading.control.application.domain.model.instrument.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MarketCatalogMapper {

    Instrument toInstrument(InstrumentDto dto);

    Asset toAsset(AssetDto dto);

    @Mapping(target = "param", ignore = true)
    ChannelCapability toChannelCapability(ChannelCapabilityDto dto);

    @Mapping(target = "allowedValue", ignore = true)
    ChannelParamCapability toChannelParamCapability(ChannelParamCapabilityDto dto);
}
