package com.trading.control.rest.mapper;

import com.trading.control.application.domain.model.*;
import com.trading.control.restapi.generated.model.*;

public final class MarketCatalogWebMapper {

    private MarketCatalogWebMapper() {
    }

    public static MarketCatalogResponseWebDto toMarketCatalogResponse(MarketCatalog catalog) {
        var dto = new MarketCatalogResponseWebDto();
        dto.setExchanges(catalog.getExchanges().stream()
                .map(MarketCatalogWebMapper::toExchangeMarket)
                .toList());
        return dto;
    }

    public static MarketInstrumentsResponseWebDto toMarketInstrumentsResponse(MarketInstruments instruments) {
        var dto = new MarketInstrumentsResponseWebDto();
        dto.setExchange(instruments.getExchange());
        dto.setMarketType(instruments.getMarketType());
        dto.setInstruments(instruments.getInstruments().stream()
                .map(MarketCatalogWebMapper::toInstrumentOption)
                .toList());
        return dto;
    }

    private static ExchangeMarketWebDto toExchangeMarket(ExchangeMarket exchange) {
        var dto = new ExchangeMarketWebDto();
        dto.setCode(exchange.getCode());
        dto.setDisplayName(exchange.getDisplayName());
        dto.setEnabled(exchange.isEnabled());
        dto.setMarketTypes(exchange.getMarketTypes().stream()
                .map(MarketCatalogWebMapper::toMarketType)
                .toList());
        return dto;
    }

    private static MarketTypeWebDto toMarketType(MarketTypeDescriptor marketType) {
        var dto = new MarketTypeWebDto();
        dto.setCode(marketType.getCode());
        dto.setDisplayName(marketType.getDisplayName());
        dto.setEnabled(marketType.isEnabled());
        dto.setStreamChannels(marketType.getStreamChannels().stream()
                .map(MarketCatalogWebMapper::toStreamChannel)
                .toList());
        return dto;
    }

    private static StreamChannelWebDto toStreamChannel(StreamChannelOption channel) {
        var dto = new StreamChannelWebDto();
        dto.setType(StreamChannelWebDto.TypeEnum.fromValue(channel.getType().name()));
        dto.setDisplayName(channel.getDisplayName());
        dto.setEnabled(channel.isEnabled());
        dto.setEnabledByDefault(channel.isEnabledByDefault());
        dto.setParams(channel.getParams().stream()
                .map(MarketCatalogWebMapper::toChannelParam)
                .toList());
        return dto;
    }

    private static ChannelParamWebDto toChannelParam(ChannelParamDefinition param) {
        var dto = new ChannelParamWebDto();
        dto.setName(param.getName());
        dto.setDisplayName(param.getDisplayName());
        dto.setType(ChannelParamWebDto.TypeEnum.fromValue(param.getType().name()));
        dto.setRequired(param.isRequired());
        dto.setDefaultValue(param.getDefaultValue());
        dto.setAllowedValues(param.getAllowedValues().stream()
                .map(MarketCatalogWebMapper::toChannelParamValue)
                .toList());
        return dto;
    }

    private static ChannelParamValueWebDto toChannelParamValue(ChannelParamAllowedValue value) {
        var dto = new ChannelParamValueWebDto();
        dto.setValue(value.getValue());
        dto.setDisplayName(value.getDisplayName());
        return dto;
    }

    private static InstrumentOptionWebDto toInstrumentOption(InstrumentOption instrument) {
        var dto = new InstrumentOptionWebDto();
        dto.setSymbol(instrument.getSymbol());
        dto.setBaseAsset(instrument.getBaseAsset());
        dto.setBaseAssetDisplayName(instrument.getBaseAssetDisplayName());
        dto.setQuoteAsset(instrument.getQuoteAsset());
        dto.setQuoteAssetDisplayName(instrument.getQuoteAssetDisplayName());
        dto.setDisplaySymbol(instrument.getDisplaySymbol());
        dto.setEnabled(instrument.isEnabled());
        return dto;
    }
}
