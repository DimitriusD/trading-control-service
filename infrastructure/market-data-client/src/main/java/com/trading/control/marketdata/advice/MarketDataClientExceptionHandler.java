package com.trading.control.marketdata.advice;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translates an open circuit breaker on the market-data-service into HTTP 503.
 * {@link CallNotPermittedException} is thrown by the resilience4j aspect before
 * the adapter body runs, so it cannot be translated inside the adapter itself.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MarketDataClientExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> handleCircuitOpen(CallNotPermittedException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "SERVICE_UNAVAILABLE");
        body.put("message", "market-data-service is temporarily unavailable");
        body.put("timestamp", OffsetDateTime.now().toString());
        return body;
    }
}
