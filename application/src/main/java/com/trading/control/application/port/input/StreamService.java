package com.trading.control.application.port.input;

import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.domain.model.CreateStreamCommand;

import java.util.List;

public interface StreamService {

    List<ConfiguredStream> getConfiguredStreams();

    ConfiguredStream createStream(CreateStreamCommand command);

    ConfiguredStream startStream(String streamId);

    ConfiguredStream stopStream(String streamId);

    void deleteStream(String streamId);
}
