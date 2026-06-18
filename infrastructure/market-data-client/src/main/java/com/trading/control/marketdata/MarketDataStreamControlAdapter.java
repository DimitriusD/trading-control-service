package com.trading.control.marketdata;

import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.port.output.MarketDataStreamControlPort;
import com.trading.mds.client.api.StreamsApi;
import com.trading.mds.client.model.UpdateStreamEnabledRequestDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketDataStreamControlAdapter implements MarketDataStreamControlPort {

    static final String CIRCUIT_BREAKER = "market-data-service";
    static final String READ_RETRY = "market-data-read";

    private final StreamsApi streamsApi;
    private final MarketDataStreamMapper mapper;

    public MarketDataStreamControlAdapter(StreamsApi streamsApi, MarketDataStreamMapper mapper) {
        this.streamsApi = streamsApi;
        this.mapper = mapper;
    }

    @Override
    @Retry(name = READ_RETRY)
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public List<StreamDefinition> listStreams() {
        return streamsApi.listStreams().stream()
                .map(mapper::toStreamDefinition)
                .toList();
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public StreamDefinition createStream(StreamDefinition command) {
        // POST is not idempotent — no @Retry to avoid creating duplicate streams on a timeout.
        var response = streamsApi.createStream(mapper.toCreateRequest(command));
        return mapper.toStreamDefinition(response);
    }

    @Override
    @Retry(name = READ_RETRY)
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public StreamDefinition setStreamEnabled(String streamId, boolean enabled) {
        var response = streamsApi.updateStreamEnabled(streamId, new UpdateStreamEnabledRequestDto().enabled(enabled));
        return mapper.toStreamDefinition(response);
    }
}
