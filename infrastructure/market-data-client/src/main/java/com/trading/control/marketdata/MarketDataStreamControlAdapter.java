package com.trading.control.marketdata;

import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.port.output.MarketDataStreamControlPort;
import com.trading.mds.client.api.StreamsApi;
import com.trading.mds.client.model.UpdateStreamEnabledRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketDataStreamControlAdapter implements MarketDataStreamControlPort {

    private final StreamsApi streamsApi;
    private final MarketDataStreamMapper mapper;

    public MarketDataStreamControlAdapter(StreamsApi streamsApi, MarketDataStreamMapper mapper) {
        this.streamsApi = streamsApi;
        this.mapper = mapper;
    }

    @Override
    public List<StreamDefinition> listStreams() {
        return streamsApi.listStreams().stream()
                .map(mapper::toStreamDefinition)
                .toList();
    }

    @Override
    public StreamDefinition createStream(StreamDefinition command) {
        var response = streamsApi.createStream(mapper.toCreateRequest(command));
        return mapper.toStreamDefinition(response);
    }

    @Override
    public StreamDefinition setStreamEnabled(String streamId, boolean enabled) {
        var response = streamsApi.updateStreamEnabled(streamId, new UpdateStreamEnabledRequestDto().enabled(enabled));
        return mapper.toStreamDefinition(response);
    }
}
