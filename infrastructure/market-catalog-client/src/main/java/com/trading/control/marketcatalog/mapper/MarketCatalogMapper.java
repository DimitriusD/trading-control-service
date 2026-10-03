package com.trading.control.marketcatalog.mapper;

import com.trading.catalog.client.model.AssetDto;
import com.trading.catalog.client.model.CatalogChannelDto;
import com.trading.catalog.client.model.CatalogChannelParamDto;
import com.trading.catalog.client.model.CatalogDto;
import com.trading.catalog.client.model.CatalogExchangeDto;
import com.trading.catalog.client.model.CatalogMarketDto;
import com.trading.catalog.client.model.CatalogParamValueDto;
import com.trading.catalog.client.model.ChannelCapabilityDto;
import com.trading.catalog.client.model.ChannelParamCapabilityDto;
import com.trading.catalog.client.model.InstrumentDto;
import com.trading.catalog.client.model.InstrumentPageDto;
import com.trading.control.application.domain.model.Asset;
import com.trading.control.application.domain.model.catalog.Catalog;
import com.trading.control.application.domain.model.catalog.CatalogChannel;
import com.trading.control.application.domain.model.catalog.CatalogChannelParam;
import com.trading.control.application.domain.model.catalog.CatalogExchange;
import com.trading.control.application.domain.model.catalog.CatalogMarket;
import com.trading.control.application.domain.model.catalog.CatalogParamValue;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.catalog.ChannelParamCapability;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.instrument.InstrumentPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MarketCatalogMapper {

    Catalog toCatalog(CatalogDto dto);

    CatalogExchange toCatalogExchange(CatalogExchangeDto dto);

    CatalogMarket toCatalogMarket(CatalogMarketDto dto);

    CatalogChannel toCatalogChannel(CatalogChannelDto dto);

    CatalogChannelParam toCatalogChannelParam(CatalogChannelParamDto dto);

    CatalogParamValue toCatalogParamValue(CatalogParamValueDto dto);

    InstrumentPage toInstrumentPage(InstrumentPageDto dto);

    Instrument toInstrument(InstrumentDto dto);

    Asset toAsset(AssetDto dto);

    @Mapping(target = "param", ignore = true)
    ChannelCapability toChannelCapability(ChannelCapabilityDto dto);

    @Mapping(target = "allowedValue", ignore = true)
    ChannelParamCapability toChannelParamCapability(ChannelParamCapabilityDto dto);
}
