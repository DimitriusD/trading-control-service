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
    private final MarketCatalogWebMapper marketCatalogWebMapper;

    public MarketsController(MarketCatalogService marketCatalogService,
                             MarketCatalogWebMapper marketCatalogWebMapper) {
        this.marketCatalogService = marketCatalogService;
        this.marketCatalogWebMapper = marketCatalogWebMapper;
    }

    @Override
    public MarketCatalogResponseWebDto getMarkets() {
        return marketCatalogWebMapper.toMarketCatalogResponse(marketCatalogService.getMarkets());
    }

    @Override
    public MarketInstrumentsResponseWebDto getMarketInstruments(String exchange, String marketType) {
        return marketCatalogWebMapper.toMarketInstrumentsResponse(
                marketCatalogService.getInstruments(exchange, marketType));
    }
}
