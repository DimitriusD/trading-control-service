package com.trading.control.marketdata;

import com.trading.control.application.domain.model.CreateStreamCommand;
import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.port.output.MarketDataStreamControlPort;
import com.trading.mds.client.api.StreamsApi;
import com.trading.mds.client.model.UpdateStreamEnabledRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketDataStreamControlAdapter implements MarketDataStreamControlPort {

    private final StreamsApi streamsApi;

    public MarketDataStreamControlAdapter(StreamsApi streamsApi) {
        this.streamsApi = streamsApi;
    }

    @Override
    public List<ConfiguredStream> listStreams() {
        return streamsApi.listStreams().stream()
                .map(MarketDataStreamMapper::toConfiguredStream)
                .toList();
    }

    @Override
    public ConfiguredStream createStream(CreateStreamCommand command) {
        var response = streamsApi.createStream(MarketDataStreamMapper.toCreateRequest(command));
        return MarketDataStreamMapper.toConfiguredStream(response);
    }

    @Override
    public ConfiguredStream setStreamEnabled(String streamId, boolean enabled) {
        var response = streamsApi.updateStreamEnabled(streamId, new UpdateStreamEnabledRequestDto().enabled(enabled));
        return MarketDataStreamMapper.toConfiguredStream(response);
    }
}
