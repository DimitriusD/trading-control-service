package com.trading.control.application.service;

import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.domain.model.CreateStreamCommand;
import com.trading.control.application.port.input.StreamService;
import com.trading.control.application.port.output.MarketDataStreamControlPort;

import java.util.List;

public class StreamServiceImpl implements StreamService {

    private final MarketDataStreamControlPort marketDataStreamControlPort;

    public StreamServiceImpl(MarketDataStreamControlPort marketDataStreamControlPort) {
        this.marketDataStreamControlPort = marketDataStreamControlPort;
    }

    @Override
    public List<ConfiguredStream> getConfiguredStreams() {
        return marketDataStreamControlPort.listStreams();
    }

    @Override
    public ConfiguredStream createStream(CreateStreamCommand command) {
        return marketDataStreamControlPort.createStream(command);
    }

    @Override
    public ConfiguredStream startStream(String streamId) {
        return marketDataStreamControlPort.setStreamEnabled(streamId, true);
    }

    @Override
    public ConfiguredStream stopStream(String streamId) {
        return marketDataStreamControlPort.setStreamEnabled(streamId, false);
    }

    @Override
    public void deleteStream(String streamId) {
        throw new UnsupportedOperationException(
                "Deleting streams is not supported by market-data-service");
    }
}
