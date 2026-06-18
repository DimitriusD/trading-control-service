package com.trading.control.application.service;

import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.port.input.StreamService;
import com.trading.control.application.port.output.MarketDataStreamControlPort;

import java.util.List;

public class StreamServiceImpl implements StreamService {

    private final MarketDataStreamControlPort marketDataStreamControlPort;

    public StreamServiceImpl(MarketDataStreamControlPort marketDataStreamControlPort) {
        this.marketDataStreamControlPort = marketDataStreamControlPort;
    }

    @Override
    public List<StreamDefinition> getConfiguredStreams() {
        return marketDataStreamControlPort.listStreams();
    }

    @Override
    public StreamDefinition createStream(StreamDefinition streamDefinition) {
        return marketDataStreamControlPort.createStream(streamDefinition);
    }

    @Override
    public StreamDefinition startStream(String streamId) {
        return marketDataStreamControlPort.setStreamEnabled(streamId, true);
    }

    @Override
    public StreamDefinition stopStream(String streamId) {
        return marketDataStreamControlPort.setStreamEnabled(streamId, false);
    }

    @Override
    public void deleteStream(String streamId) {
        throw new UnsupportedOperationException(
                "Deleting streams is not supported by market-data-service");
    }
}
