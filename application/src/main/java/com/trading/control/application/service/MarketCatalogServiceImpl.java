package com.trading.control.application.service;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.model.ExchangeMarket;
import com.trading.control.application.domain.model.MarketCatalog;
import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.domain.model.MarketTypeDescriptor;
import com.trading.control.application.domain.model.catalog.Exchange;
import com.trading.control.application.domain.model.catalog.ExchangeMarketLink;
import com.trading.control.application.domain.model.catalog.MarketType;
import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.application.port.output.CatalogStorePort;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MarketCatalogServiceImpl implements MarketCatalogService {

    private final CatalogStorePort catalogStore;

    public MarketCatalogServiceImpl(CatalogStorePort catalogStore) {
        this.catalogStore = catalogStore;
    }

    @Override
    public MarketCatalog getMarkets() {
        Map<String, MarketType> marketTypesByCode = catalogStore.findMarketTypes().stream()
                .collect(Collectors.toMap(MarketType::getCode, Function.identity()));

        Map<String, List<ExchangeMarketLink>> linksByExchange = catalogStore.findExchangeMarkets().stream()
                .collect(Collectors.groupingBy(ExchangeMarketLink::getExchangeCode));

        MarketCatalog.MarketCatalogBuilder catalog = MarketCatalog.builder();
        for (Exchange exchange : catalogStore.findExchanges()) {
            ExchangeMarket.ExchangeMarketBuilder market = ExchangeMarket.builder()
                    .code(exchange.getCode())
                    .displayName(exchange.getName())
                    .enabled(exchange.isEnabled());

            for (ExchangeMarketLink link : linksByExchange.getOrDefault(exchange.getCode(), List.of())) {
                MarketType marketType = marketTypesByCode.get(link.getMarketTypeCode());
                String displayName = marketType != null ? marketType.getName() : link.getMarketTypeCode();
                market.marketType(MarketTypeDescriptor.builder()
                        .code(link.getMarketTypeCode())
                        .displayName(displayName)
                        .enabled(link.isEnabled())
                        .streamChannels(StaticMarketCatalog.defaultStreamChannels())
                        .build());
            }
            catalog.exchange(market.build());
        }
        return catalog.build();
    }

    @Override
    public MarketInstruments getInstruments(String exchange, String marketType) {
        if (!StaticMarketCatalog.hasExchange(exchange)) {
            throw new NotFoundException("Exchange not found: " + exchange);
        }
        if (!StaticMarketCatalog.hasMarketType(exchange, marketType)) {
            throw new NotFoundException("Market type not found: " + marketType + " for exchange: " + exchange);
        }

        return MarketInstruments.builder()
                .exchange(exchange)
                .marketType(marketType)
                .instruments(StaticMarketCatalog.getInstruments(exchange, marketType))
                .build();
    }
}
