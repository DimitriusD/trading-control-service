package com.trading.control.config;

import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.application.port.input.StreamService;
import com.trading.control.application.service.MarketCatalogServiceImpl;
import com.trading.control.application.service.StreamServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    @Bean
    public MarketCatalogService marketCatalogUseCase() {
        return new MarketCatalogServiceImpl();
    }

    @Bean
    public StreamService streamUseCase() {
        return new StreamServiceImpl();
    }
}
