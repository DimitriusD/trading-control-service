package com.trading.control.marketcatalog.config;

import com.trading.catalog.client.api.InstrumentsApi;
import com.trading.catalog.client.api.MarketsApi;
import com.trading.catalog.client.invoker.ApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MarketCatalogServiceClientProperties.class)
public class MarketCatalogServiceClientConfiguration {

    @Bean
    ApiClient marketCatalogServiceApiClient(MarketCatalogServiceClientProperties properties) {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withTimeouts(properties.connectTimeout(), properties.readTimeout());
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings);

        RestClient restClient = ApiClient.buildRestClientBuilder()
                .requestFactory(requestFactory)
                .build();

        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(properties.url());
        return apiClient;
    }

    @Bean
    InstrumentsApi marketCatalogInstrumentsApi(ApiClient marketCatalogServiceApiClient) {
        return new InstrumentsApi(marketCatalogServiceApiClient);
    }

    @Bean
    MarketsApi marketCatalogMarketsApi(ApiClient marketCatalogServiceApiClient) {
        return new MarketsApi(marketCatalogServiceApiClient);
    }
}
