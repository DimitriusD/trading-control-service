package com.trading.control.application.port.output;

import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;

import java.util.List;

public interface MarketDataStreamControlPort {

    List<StreamDefinition> listStreams();

    StreamDefinition getStream(String streamId);

    StreamDefinition createStream(StreamDefinition command);

    StreamDefinition updateStream(String streamId, StreamPatch patch);

    void deleteStream(String streamId);
}
