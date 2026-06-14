package com.trading.control.rest;

import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.rest.mapper.MarketCatalogWebMapper;
import com.trading.control.restapi.generated.api.MarketsApi;
import com.trading.control.restapi.generated.model.MarketCatalogResponseWebDto;
import com.trading.control.restapi.generated.model.MarketInstrumentsResponseWebDto;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarketsController implements MarketsApi {

    private final MarketCatalogService marketCatalogService;

    public MarketsController(MarketCatalogService marketCatalogService) {
        this.marketCatalogService = marketCatalogService;
    }

    @Override
    public MarketCatalogResponseWebDto getMarkets() {
        return MarketCatalogWebMapper.toMarketCatalogResponse(
                marketCatalogService.getMarkets());
    }

    @Override
    public MarketInstrumentsResponseWebDto getMarketInstruments(String exchange, String marketType) {
        return MarketCatalogWebMapper.toMarketInstrumentsResponse(
                marketCatalogService.getInstruments(exchange, marketType));
    }
}
