package com.trading.control.marketdata;

import com.trading.mds.client.api.StreamsApi;
import com.trading.mds.client.invoker.ApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MarketDataServiceClientProperties.class)
public class MarketDataServiceClientConfiguration {

    @Bean
    ApiClient marketDataServiceApiClient(MarketDataServiceClientProperties properties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(properties.baseUrl());
        return apiClient;
    }

    @Bean
    StreamsApi marketDataStreamsApi(ApiClient marketDataServiceApiClient) {
        return new StreamsApi(marketDataServiceApiClient);
    }
}
