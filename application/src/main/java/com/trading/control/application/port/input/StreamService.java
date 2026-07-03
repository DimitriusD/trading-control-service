package com.trading.control.application.port.input;

import com.trading.control.application.domain.model.stream.CreateStreamCommand;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;

import java.util.List;

public interface StreamService {

    List<StreamDefinition> getStreams();

    StreamDefinition getStream(String streamId);

    StreamDefinition createStream(CreateStreamCommand command);

    StreamDefinition updateStream(String streamId, StreamPatch patch);

    void deleteStream(String streamId);
}
