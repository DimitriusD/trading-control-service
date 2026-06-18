package com.trading.control.config;

import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.application.port.input.StreamService;
import com.trading.control.application.port.output.CatalogStorePort;
import com.trading.control.application.port.output.MarketDataStreamControlPort;
import com.trading.control.application.service.MarketCatalogServiceImpl;
import com.trading.control.application.service.StreamServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    @Bean
    public MarketCatalogService marketCatalogUseCase(CatalogStorePort catalogStorePort) {
        return new MarketCatalogServiceImpl(catalogStorePort);
    }

    @Bean
    public StreamService streamUseCase(MarketDataStreamControlPort marketDataStreamControlPort) {
        return new StreamServiceImpl(marketDataStreamControlPort);
    }
}
