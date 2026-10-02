package com.trading.control.marketcatalog;

import com.trading.catalog.client.api.InstrumentsApi;
import com.trading.catalog.client.api.MarketsApi;
import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.exception.ServiceUnavailableException;
import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.port.output.MarketCatalogPort;
import com.trading.control.marketcatalog.mapper.MarketCatalogMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.function.Supplier;

@Component
public class MarketCatalogAdapter implements MarketCatalogPort {

    static final String CIRCUIT_BREAKER = "market-catalog-service";
    static final String READ_RETRY = "market-catalog-read";

    private final InstrumentsApi instrumentsApi;
    private final MarketsApi marketsApi;
    private final MarketCatalogMapper mapper;

    public MarketCatalogAdapter(InstrumentsApi instrumentsApi, MarketsApi marketsApi, MarketCatalogMapper mapper) {
        this.instrumentsApi = instrumentsApi;
        this.marketsApi = marketsApi;
        this.mapper = mapper;
    }

    @Override
    @Retry(name = READ_RETRY)
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public Instrument getInstrument(String instrumentId) {
        return call(() -> mapper.toInstrument(instrumentsApi.getInstrument(instrumentId)),
                "Instrument not found: " + instrumentId);
    }

    @Override
    @Retry(name = READ_RETRY)
    @CircuitBreaker(name = CIRCUIT_BREAKER)
    public List<ChannelCapability> getChannelCapabilities(String exchangeCode, String marketCode) {
        return call(() -> marketsApi.getChannelCapabilities(exchangeCode, marketCode).stream()
                        .map(mapper::toChannelCapability)
                        .toList(),
                "Market not found: " + exchangeCode + "/" + marketCode);
    }

    /**
     * Executes a catalog call and translates transport/HTTP failures into domain
     * exceptions: 404 → 404, 400 → 400, 5xx / connection failures → 503.
     */
    private <T> T call(Supplier<T> action, String notFoundMessage) {
        try {
            return action.get();
        } catch (RestClientResponseException ex) {
            throw translate(ex, notFoundMessage);
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException("market-catalog-service is unavailable", ex);
        }
    }

    private RuntimeException translate(RestClientResponseException ex, String notFoundMessage) {
        HttpStatusCode status = ex.getStatusCode();
        if (status.value() == 404) {
            return new NotFoundException(notFoundMessage);
        }
        if (status.value() == 400) {
            return new ValidationException("market-catalog-service rejected the request: " + ex.getResponseBodyAsString());
        }
        return new ServiceUnavailableException("market-catalog-service error: " + status);
    }
}
