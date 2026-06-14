package com.trading.control.application.port.output;

import com.trading.control.application.domain.model.CreateStreamCommand;
import com.trading.control.application.domain.model.stream.ConfiguredStream;

import java.util.List;

public interface MarketDataStreamControlPort {

    List<ConfiguredStream> listStreams();

    ConfiguredStream createStream(CreateStreamCommand command);

    ConfiguredStream setStreamEnabled(String streamId, boolean enabled);
}
