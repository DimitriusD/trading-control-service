package com.trading.control.rest.mapper;

import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.domain.model.CreateStreamCommand;
import com.trading.control.application.domain.model.CreateStreamInstrument;
import com.trading.control.application.domain.model.enums.MarketDataChannelType;
import com.trading.control.application.domain.model.stream.chanel.StreamChannelConfig;
import com.trading.control.restapi.generated.model.ConfiguredStreamWebDto;
import com.trading.control.restapi.generated.model.CreateStreamInstrumentWebDto;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import com.trading.control.restapi.generated.model.StreamChannelConfigWebDto;

import java.util.List;
import java.util.Map;

public final class StreamWebMapper {

    private StreamWebMapper() {
    }

    public static CreateStreamCommand toCreateStreamCommand(CreateStreamRequestWebDto request) {
        CreateStreamInstrumentWebDto instrument = request.getInstrument();
        return CreateStreamCommand.builder()
                .exchange(request.getExchange())
                .marketType(request.getMarketType())
                .instrument(CreateStreamInstrument.builder()
                        .symbol(instrument.getSymbol())
                        .baseAsset(instrument.getBaseAsset())
                        .quoteAsset(instrument.getQuoteAsset())
                        .instrumentId(instrument.getInstrumentId())
                        .build())
                .channels(toChannelConfigs(request.getChannels()))
                .startImmediately(Boolean.TRUE.equals(request.getStartImmediately()))
                .autoRestart(Boolean.TRUE.equals(request.getAutoRestart()))
                .build();
    }

    private static List<StreamChannelConfig> toChannelConfigs(List<StreamChannelConfigWebDto> channels) {
        if (channels == null) {
            return List.of();
        }
        return channels.stream()
                .map(StreamWebMapper::toChannelConfig)
                .toList();
    }

    private static StreamChannelConfig toChannelConfig(StreamChannelConfigWebDto channel) {
        return StreamChannelConfig.builder()
                .type(MarketDataChannelType.valueOf(channel.getType().getValue()))
                .params(channel.getParams() == null ? Map.of() : channel.getParams())
                .build();
    }

    public static ConfiguredStreamWebDto toConfiguredStream(ConfiguredStream stream) {
        var dto = new ConfiguredStreamWebDto();


        dto.setId(stream.getId());
        dto.setInstrumentId(stream.getInstrumentId());
        dto.setInstrumentKey(stream.getInstrumentKey());
        dto.setPair(stream.getPair());
        dto.setSymbol(stream.getSymbol());
        dto.setExchange(stream.getExchange());
        dto.setMarket(stream.getMarket());
        dto.setBaseAsset(stream.getBaseAsset());
        dto.setQuoteAsset(stream.getQuoteAsset());
        dto.setChannels(stream.getChannels().stream()
                .map(StreamWebMapper::toStreamChannelConfig)
                .toList());
        dto.setDesired(ConfiguredStreamWebDto.DesiredEnum.fromValue(stream.getDesired().name()));
        dto.setRuntime(ConfiguredStreamWebDto.RuntimeEnum.fromValue(stream.getRuntime().name()));
        dto.setHealth(ConfiguredStreamWebDto.HealthEnum.fromValue(stream.getHealth().name()));
        dto.setAutoRestart(stream.isAutoRestart());
        return dto;
    }

    private static StreamChannelConfigWebDto toStreamChannelConfig(StreamChannelConfig channel) {
        var dto = new StreamChannelConfigWebDto();
        dto.setType(StreamChannelConfigWebDto.TypeEnum.fromValue(channel.getType().name()));
        dto.setParams(channel.getParams());
        return dto;
    }
}
