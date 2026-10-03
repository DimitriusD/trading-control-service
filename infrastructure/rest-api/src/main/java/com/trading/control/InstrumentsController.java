package com.trading.control;

import com.trading.control.application.domain.model.instrument.InstrumentSearchQuery;
import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.mapper.CatalogWebMapper;
import com.trading.control.restapi.generated.api.InstrumentsApi;
import com.trading.control.restapi.generated.model.InstrumentPageWebDto;
import com.trading.control.restapi.generated.model.InstrumentWebDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class InstrumentsController implements InstrumentsApi {

    private final MarketCatalogService marketCatalogService;
    private final CatalogWebMapper catalogWebMapper;

    @Override
    public InstrumentPageWebDto searchInstruments(String exchangeCode, String marketCode, String searchText,
                                                  String baseAssetCode, String quoteAssetCode,
                                                  Integer limit, String cursor) {
        final var query = new InstrumentSearchQuery(exchangeCode, marketCode, searchText,
                baseAssetCode, quoteAssetCode, limit, cursor);
        return catalogWebMapper.toInstrumentPage(marketCatalogService.searchInstruments(query));
    }

    @Override
    public InstrumentWebDto getInstrument(String instrumentId) {
        return catalogWebMapper.toInstrument(marketCatalogService.getInstrument(instrumentId));
    }
}
