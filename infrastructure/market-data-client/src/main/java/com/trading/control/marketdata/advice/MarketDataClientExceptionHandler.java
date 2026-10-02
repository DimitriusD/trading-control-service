package com.trading.control.marketdata.advice;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class MarketDataClientExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, Object> handleCircuitOpen(CallNotPermittedException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "SERVICE_UNAVAILABLE");
        body.put("message", ex.getCausingCircuitBreakerName() + " is temporarily unavailable");
        body.put("timestamp", OffsetDateTime.now(clock).toString());
        return body;
    }
}
