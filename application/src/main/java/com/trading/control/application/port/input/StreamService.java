package com.trading.control.application.port.input;

import com.trading.control.application.domain.model.stream.StreamDefinition;

import java.util.List;

public interface StreamService {

    List<StreamDefinition> getConfiguredStreams();

    StreamDefinition createStream(StreamDefinition streamDefinition);

    StreamDefinition startStream(String streamId);

    StreamDefinition stopStream(String streamId);

    void deleteStream(String streamId);
}
