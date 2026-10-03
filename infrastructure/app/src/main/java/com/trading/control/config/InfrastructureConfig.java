package com.trading.control.config;

import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.application.port.input.StreamService;
import com.trading.control.application.port.output.MarketCatalogPort;
import com.trading.control.application.port.output.MarketDataStreamControlPort;
import com.trading.control.application.service.MarketCatalogServiceImpl;
import com.trading.control.application.service.StreamCommandValidator;
import com.trading.control.application.service.StreamServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class InfrastructureConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public MarketCatalogService marketCatalogService(MarketCatalogPort marketCatalogPort) {
        return new MarketCatalogServiceImpl(marketCatalogPort);
    }

    @Bean
    public StreamCommandValidator streamCommandValidator(MarketCatalogPort marketCatalogPort) {
        return new StreamCommandValidator(marketCatalogPort);
    }

    @Bean
    public StreamService streamUseCase(MarketDataStreamControlPort marketDataStreamControlPort,
                                       MarketCatalogPort marketCatalogPort,
                                       StreamCommandValidator streamCommandValidator) {
        return new StreamServiceImpl(marketDataStreamControlPort, marketCatalogPort, streamCommandValidator);
    }
}
