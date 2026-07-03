package com.trading.control.marketdata;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.exception.ServiceUnavailableException;
import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;
import com.trading.control.application.port.output.MarketDataStreamControlPort;
import com.trading.control.marketdata.mapper.MarketDataStreamMapper;
import com.trading.mds.client.api.StreamsApi;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.function.Supplier;

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
        return call(() -> streamsApi.listStreams().stream()
                .map(mapper::toStreamDefinition)
                .toList());
    }

    @Override
    @Retry(name = READ_RETRY)
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public StreamDefinition getStream(String streamId) {
        return call(() -> mapper.toStreamDefinition(streamsApi.getStream(streamId)));
    }

    @Override
    @Retry(name = READ_RETRY)
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public StreamDefinition createStream(StreamDefinition command) {
        // MDS POST /streams is a declarative create-or-replace with a deterministic
        // body, so retrying a transient failure is safe.
        return call(() -> mapper.toStreamDefinition(streamsApi.createStream(mapper.toCreateRequest(command))));
    }

    @Override
    @Retry(name = READ_RETRY)
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public StreamDefinition updateStream(String streamId, StreamPatch patch) {
        // PATCH is declarative/idempotent, so retry is acceptable.
        return call(() -> mapper.toStreamDefinition(streamsApi.updateStream(streamId, mapper.toUpdateRequest(patch))));
    }

    @Override
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public void deleteStream(String streamId) {
        // No retry: a retried delete can turn a successful-but-slow first call into a spurious 404.
        call(() -> {
            streamsApi.deleteStream(streamId);
            return null;
        });
    }

    /**
     * Executes an MDS call and translates transport/HTTP failures into domain
     * exceptions so callers keep meaningful status codes:
     * MDS 404 → 404, MDS 400 → 400, MDS 5xx / connection failures → 503.
     */
    private <T> T call(Supplier<T> action) {
        try {
            return action.get();
        } catch (RestClientResponseException ex) {
            throw translate(ex);
        } catch (ResourceAccessException ex) {
            // connection refused / read timeout
            throw new ServiceUnavailableException("market-data-service is unavailable", ex);
        }
    }

    private RuntimeException translate(RestClientResponseException ex) {
        HttpStatusCode status = ex.getStatusCode();
        if (status.value() == 404) {
            return new NotFoundException("market-data-service resource not found");
        }
        if (status.value() == 400) {
            return new ValidationException("market-data-service rejected the request: " + ex.getResponseBodyAsString());
        }
        return new ServiceUnavailableException("market-data-service error: " + status);
    }
}
