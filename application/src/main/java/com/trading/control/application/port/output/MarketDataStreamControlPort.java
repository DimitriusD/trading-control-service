package com.trading.control.application.port.output;

import com.trading.control.application.domain.model.stream.StreamDefinition;

import java.util.List;

public interface MarketDataStreamControlPort {

    List<StreamDefinition> listStreams();

    StreamDefinition createStream(StreamDefinition command);

    StreamDefinition setStreamEnabled(String streamId, boolean enabled);
}
