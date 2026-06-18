package com.trading.control.marketdata;

import com.trading.mds.client.api.StreamsApi;
import com.trading.mds.client.invoker.ApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MarketDataServiceClientProperties.class)
public class MarketDataServiceClientConfiguration {

    @Bean
    ApiClient marketDataServiceApiClient(MarketDataServiceClientProperties properties) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(properties.connectTimeout())
                .withReadTimeout(properties.readTimeout());
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        RestClient restClient = ApiClient.buildRestClientBuilder()
                .requestFactory(requestFactory)
                .build();

        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(properties.baseUrl());
        return apiClient;
    }

    @Bean
    StreamsApi marketDataStreamsApi(ApiClient marketDataServiceApiClient) {
        return new StreamsApi(marketDataServiceApiClient);
    }
}
