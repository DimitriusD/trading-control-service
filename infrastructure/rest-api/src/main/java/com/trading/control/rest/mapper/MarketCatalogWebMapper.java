package com.trading.control.rest.mapper;

import com.trading.control.application.domain.model.Asset;
import com.trading.control.application.domain.model.Channel;
import com.trading.control.application.domain.model.ChannelParam;
import com.trading.control.application.domain.model.ChannelParamValue;
import com.trading.control.application.domain.model.ExchangeMarket;
import com.trading.control.application.domain.model.Instrument;
import com.trading.control.application.domain.model.MarketCatalog;
import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.domain.model.MarketType;
import com.trading.control.restapi.generated.model.ChannelParamValueWebDto;
import com.trading.control.restapi.generated.model.ChannelParamWebDto;
import com.trading.control.restapi.generated.model.ChannelWebDto;
import com.trading.control.restapi.generated.model.AssetWebDto;
import com.trading.control.restapi.generated.model.ExchangeWebDto;
import com.trading.control.restapi.generated.model.InstrumentWebDto;
import com.trading.control.restapi.generated.model.MarketCatalogResponseWebDto;
import com.trading.control.restapi.generated.model.MarketInstrumentsResponseWebDto;
import com.trading.control.restapi.generated.model.MarketTypeWebDto;

public final class MarketCatalogWebMapper {

    private MarketCatalogWebMapper() {
    }

    public static MarketCatalogResponseWebDto toMarketCatalogResponse(MarketCatalog catalog) {
        var dto = new MarketCatalogResponseWebDto();
        dto.setExchanges(catalog.getExchanges().stream()
                .map(MarketCatalogWebMapper::toExchange)
                .toList());
        return dto;
    }

    public static MarketInstrumentsResponseWebDto toMarketInstrumentsResponse(MarketInstruments instruments) {
        var dto = new MarketInstrumentsResponseWebDto();
        dto.setExchange(instruments.getExchange());
        dto.setMarketType(instruments.getMarketType());
        dto.setInstruments(instruments.getInstruments().stream()
                .map(MarketCatalogWebMapper::toInstrument)
                .toList());
        return dto;
    }

    private static ExchangeWebDto toExchange(ExchangeMarket exchange) {
        var dto = new ExchangeWebDto();
        dto.setCode(exchange.getCode());
        dto.setDisplayName(exchange.getDisplayName());
        dto.setEnabled(exchange.isEnabled());
        dto.setMarketTypes(exchange.getMarketTypes().stream()
                .map(MarketCatalogWebMapper::toMarketType)
                .toList());
        return dto;
    }

    private static MarketTypeWebDto toMarketType(MarketType marketType) {
        var dto = new MarketTypeWebDto();
        dto.setCode(marketType.getCode());
        dto.setDisplayName(marketType.getDisplayName());
        dto.setEnabled(marketType.isEnabled());
        dto.setChannels(marketType.getChannels().stream()
                .map(MarketCatalogWebMapper::toChannel)
                .toList());
        return dto;
    }

    private static ChannelWebDto toChannel(Channel channel) {
        var dto = new ChannelWebDto();
        dto.setCode(channel.getCode());
        dto.setName(channel.getName());
        dto.setEnabled(channel.isEnabled());
        dto.setParams(channel.getParams().stream()
                .map(MarketCatalogWebMapper::toChannelParam)
                .toList());
        return dto;
    }

    private static ChannelParamWebDto toChannelParam(ChannelParam param) {
        var dto = new ChannelParamWebDto();
        dto.setKey(param.getKey());
        dto.setValues(param.getValues().stream()
                .map(MarketCatalogWebMapper::toChannelParamValue)
                .toList());
        return dto;
    }

    private static ChannelParamValueWebDto toChannelParamValue(ChannelParamValue value) {
        var dto = new ChannelParamValueWebDto();
        dto.setValue(value.getValue());
        dto.setDisplayName(value.getDisplayName());
        dto.setDefault(value.isDefaultFlag());
        return dto;
    }

    private static InstrumentWebDto toInstrument(Instrument instrument) {
        var dto = new InstrumentWebDto();
        dto.setInstrumentId(instrument.getInstrumentId());
        dto.setBaseAsset(toAsset(instrument.getBaseAsset()));
        dto.setQuoteAsset(toAsset(instrument.getQuoteAsset()));
        dto.setDisplaySymbol(instrument.getDisplaySymbol());
        dto.setEnabled(instrument.isEnabled());
        return dto;
    }

    private static AssetWebDto toAsset(Asset asset) {
        if (asset == null) {
            return null;
        }
        var dto = new AssetWebDto();
        dto.setCode(asset.getCode());
        dto.setName(asset.getName());
        return dto;
    }
}
