package com.trading.control.marketdata;

import com.trading.control.application.domain.model.CreateStreamCommand;
import com.trading.control.application.domain.model.CreateStreamInstrument;
import com.trading.control.application.domain.model.enums.MarketDataChannelType;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamHealthState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.domain.model.stream.chanel.StreamChannelConfig;
import com.trading.mds.client.model.ChannelDefinitionDto;
import com.trading.mds.client.model.ChannelTypeDto;
import com.trading.mds.client.model.CreateStreamRequestDto;
import com.trading.mds.client.model.ExchangeDto;
import com.trading.mds.client.model.InstrumentIdDto;
import com.trading.mds.client.model.MarketTypeDto;
import com.trading.mds.client.model.StreamResponseDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

final class MarketDataStreamMapper {

    private MarketDataStreamMapper() {
    }

    static CreateStreamRequestDto toCreateRequest(CreateStreamCommand command) {
        CreateStreamInstrument instrument = command.getInstrument();

        InstrumentIdDto instrumentId = new InstrumentIdDto()
                .exchange(ExchangeDto.fromValue(command.getExchange()))
                .market(MarketTypeDto.fromValue(command.getMarketType()))
                .base(instrument.getBaseAsset())
                .quote(instrument.getQuoteAsset());

        List<ChannelDefinitionDto> channels = command.getChannels().stream()
                .map(MarketDataStreamMapper::toChannelDefinition)
                .toList();

        return new CreateStreamRequestDto()
                .instrument(instrumentId)
                .channels(channels)
                .enabled(command.isStartImmediately());
    }

    static ConfiguredStream toConfiguredStream(StreamResponseDto dto) {
        InstrumentIdDto instrument = dto.getInstrument();
        String exchange = instrument.getExchange().getValue();
        String market = instrument.getMarket().getValue();
        String base = instrument.getBase();
        String quote = instrument.getQuote();
        boolean enabled = Boolean.TRUE.equals(dto.getEnabled());

        ConfiguredStream.ConfiguredStreamBuilder builder = ConfiguredStream.builder()
                .id(dto.getStreamId())
                .instrumentId(String.join(":", exchange, market, base, quote))
                .instrumentKey(String.join("_", exchange, market, base, quote))
                .pair(base + "/" + quote)
                .symbol(base + quote)
                .exchange(exchange)
                .market(market)
                .baseAsset(base)
                .quoteAsset(quote)
                .desired(enabled ? StreamDesiredState.ENABLED : StreamDesiredState.DISABLED)
                .runtime(enabled ? StreamRuntimeState.RUNNING : StreamRuntimeState.STOPPED)
                .health(StreamHealthState.UNKNOWN)
                .autoRestart(false);

        List<ChannelDefinitionDto> channels = dto.getChannels();
        if (channels != null) {
            channels.stream()
                    .map(MarketDataStreamMapper::toChannelConfig)
                    .flatMap(Optional::stream)
                    .forEach(builder::channel);
        }
        return builder.build();
    }

    private static ChannelDefinitionDto toChannelDefinition(StreamChannelConfig channel) {
        return new ChannelDefinitionDto()
                .type(ChannelTypeDto.fromValue(channel.getType().name()))
                .enabled(true)
                .params(channel.getParams() == null ? Map.of() : channel.getParams());
    }

    private static Optional<StreamChannelConfig> toChannelConfig(ChannelDefinitionDto dto) {
        return toChannelType(dto.getType())
                .map(type -> StreamChannelConfig.builder()
                        .type(type)
                        .params(dto.getParams() == null ? Map.of() : dto.getParams())
                        .build());
    }

    private static Optional<MarketDataChannelType> toChannelType(ChannelTypeDto type) {
        if (type == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(MarketDataChannelType.valueOf(type.getValue()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
