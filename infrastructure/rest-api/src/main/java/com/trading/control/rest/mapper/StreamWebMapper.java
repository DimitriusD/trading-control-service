package com.trading.control.rest.mapper;

import com.trading.control.application.domain.model.CreateStreamCommand;
import com.trading.control.application.domain.model.CreateStreamInstrument;
import com.trading.control.application.domain.model.enums.MarketDataChannelType;
import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.domain.model.stream.chanel.StreamChannelConfig;
import com.trading.control.restapi.generated.model.ChannelParamValueWebDto;
import com.trading.control.restapi.generated.model.ChannelParamWebDto;
import com.trading.control.restapi.generated.model.ChannelWebDto;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import com.trading.control.restapi.generated.model.InstrumentIdWebDto;
import com.trading.control.restapi.generated.model.StreamResponseWebDto;

import java.util.List;
import java.util.Map;

public final class StreamWebMapper {

    private StreamWebMapper() {
    }

    // ===== inbound: CreateStreamRequest -> CreateStreamCommand =====

    public static CreateStreamCommand toCreateStreamCommand(CreateStreamRequestWebDto request) {
        InstrumentIdWebDto instrument = request.getInstrument();
        return CreateStreamCommand.builder().exchange(instrument.getExchange()).marketType(instrument.getMarket()).instrument(CreateStreamInstrument.builder().baseAsset(instrument.getBaseAsset()).quoteAsset(instrument.getQuoteAsset()).build()).channels(toChannelConfigs(request.getChannels())).startImmediately(Boolean.TRUE.equals(request.getStartImmediately())).build();
    }

    private static List<StreamChannelConfig> toChannelConfigs(List<ChannelWebDto> channels) {
        if (channels == null) {
            return List.of();
        }
        return channels.stream().map(StreamWebMapper::toChannelConfig).toList();
    }

    private static StreamChannelConfig toChannelConfig(ChannelWebDto channel) {
        StreamChannelConfig.StreamChannelConfigBuilder builder = StreamChannelConfig.builder().type(MarketDataChannelType.valueOf(channel.getCode()));
        if (channel.getParams() != null) {
            for (ChannelParamWebDto param : channel.getParams()) {
                builder.param(param.getKey(), selectedValue(param));
            }
        }
        return builder.build();
    }

    /**
     * Resolve a param's configured value: the one flagged default, else the first available.
     */
    private static String selectedValue(ChannelParamWebDto param) {
        List<ChannelParamValueWebDto> values = param.getValues();
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream().filter(v -> Boolean.TRUE.equals(v.getIsDefault())).map(ChannelParamValueWebDto::getValue).findFirst().orElseGet(() -> values.get(0).getValue());
    }

    // ===== outbound: ConfiguredStream -> StreamResponse =====

    public static StreamResponseWebDto toStreamResponse(ConfiguredStream stream) {
        var dto = new StreamResponseWebDto();
        dto.setStreamId(stream.getId());
        dto.setInstrument(toInstrumentId(stream));
        dto.setChannels(stream.getChannels().stream().map(StreamWebMapper::toChannel).toList());
        dto.setDesired(StreamResponseWebDto.DesiredEnum.fromValue(stream.getDesired().name()));
        dto.setRuntime(StreamResponseWebDto.RuntimeEnum.fromValue(stream.getRuntime().name()));
        dto.setHealth(StreamResponseWebDto.HealthEnum.fromValue(stream.getHealth().name()));
        return dto;
    }

    private static InstrumentIdWebDto toInstrumentId(ConfiguredStream stream) {
        var dto = new InstrumentIdWebDto();
        dto.setExchange(stream.getExchange());
        dto.setMarket(stream.getMarket());
        dto.setBaseAsset(stream.getBaseAsset());
        dto.setQuoteAsset(stream.getQuoteAsset());
        return dto;
    }

    private static ChannelWebDto toChannel(StreamChannelConfig channel) {
        var dto = new ChannelWebDto();
        dto.setCode(channel.getType().name());
        dto.setName(channel.getType().name());
        dto.setEnabled(true);
        dto.setParams(channel.getParams().entrySet().stream().map(StreamWebMapper::toChannelParam).toList());
        return dto;
    }

    private static ChannelParamWebDto toChannelParam(Map.Entry<String, String> entry) {
        var value = new ChannelParamValueWebDto();
        value.setValue(entry.getValue());
        value.setDisplayName(entry.getValue());
        value.isDefault(true);

        var dto = new ChannelParamWebDto();
        dto.setKey(entry.getKey());
        dto.setValues(List.of(value));
        return dto;
    }
}
