package com.trading.control;

import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.mapper.MarketCatalogWebMapper;
import com.trading.control.restapi.generated.api.MarketsApi;
import com.trading.control.restapi.generated.model.ExchangeWebDto;
import com.trading.control.restapi.generated.model.MarketInstrumentsResponseWebDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class MarketsController implements MarketsApi {

    private final MarketCatalogService marketCatalogService;
    private final MarketCatalogWebMapper marketCatalogWebMapper;

    @Override
    public List<ExchangeWebDto> getMarkets() {
        return marketCatalogWebMapper.toMarketCatalog(marketCatalogService.getMarkets());
    }

    @Override
    public MarketInstrumentsResponseWebDto getMarketInstruments(String exchange, String marketType) {
        final var instruments = marketCatalogService.getInstruments(exchange, marketType);
        return marketCatalogWebMapper.toMarketInstrumentsResponse(instruments);
    }
}
