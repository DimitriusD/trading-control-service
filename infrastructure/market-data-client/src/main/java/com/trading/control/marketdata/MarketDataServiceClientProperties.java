package com.trading.control.marketdata;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "market-data-service")
public record MarketDataServiceClientProperties(String baseUrl) {
}
