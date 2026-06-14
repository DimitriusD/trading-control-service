package com.trading.control.application.service;

import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.domain.model.CreateStreamCommand;
import com.trading.control.application.domain.model.CreateStreamInstrument;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamHealthState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.port.input.StreamService;

import java.util.List;
import java.util.UUID;

public class StreamServiceImpl implements StreamService {

    @Override
    public List<ConfiguredStream> getConfiguredStreams() {
        return StaticStreams.getConfiguredStreams();
    }

    @Override
    public ConfiguredStream createStream(CreateStreamCommand command) {
        CreateStreamInstrument instrument = command.getInstrument();
        boolean start = command.isStartImmediately();

        return ConfiguredStream.builder()
                .id("stream-" + UUID.randomUUID())
                .instrumentId(instrument.getInstrumentId())
                .instrumentKey(buildInstrumentKey(command, instrument))
                .pair(instrument.getBaseAsset() + "/" + instrument.getQuoteAsset())
                .symbol(instrument.getSymbol())
                .exchange(command.getExchange())
                .market(command.getMarketType())
                .baseAsset(instrument.getBaseAsset())
                .quoteAsset(instrument.getQuoteAsset())
                .channels(command.getChannels())
                .desired(start ? StreamDesiredState.ENABLED : StreamDesiredState.DISABLED)
                .runtime(start ? StreamRuntimeState.STARTING : StreamRuntimeState.STOPPED)
                .health(StreamHealthState.UNKNOWN)
                .autoRestart(command.isAutoRestart())
                .build();
    }

    @Override
    public ConfiguredStream startStream(String streamId) {
        return findById(streamId).toBuilder()
                .desired(StreamDesiredState.ENABLED)
                .runtime(StreamRuntimeState.STARTING)
                .build();
    }

    @Override
    public ConfiguredStream stopStream(String streamId) {
        return findById(streamId).toBuilder()
                .desired(StreamDesiredState.DISABLED)
                .runtime(StreamRuntimeState.STOPPING)
                .build();
    }

    @Override
    public void deleteStream(String streamId) {
        findById(streamId);
    }

    private static ConfiguredStream findById(String streamId) {
        return StaticStreams.getConfiguredStreams().stream()
                .filter(s -> s.getId().equals(streamId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Stream not found: " + streamId));
    }

    private static String buildInstrumentKey(CreateStreamCommand command, CreateStreamInstrument instrument) {
        return String.join("_",
                command.getExchange(),
                command.getMarketType(),
                instrument.getBaseAsset(),
                instrument.getQuoteAsset());
    }
}
