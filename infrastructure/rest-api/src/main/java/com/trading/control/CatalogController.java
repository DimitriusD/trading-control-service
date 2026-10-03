package com.trading.control;

import com.trading.control.application.port.input.MarketCatalogService;
import com.trading.control.mapper.CatalogWebMapper;
import com.trading.control.restapi.generated.api.CatalogApi;
import com.trading.control.restapi.generated.model.CatalogWebDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CatalogController implements CatalogApi {

    private final MarketCatalogService marketCatalogService;
    private final CatalogWebMapper catalogWebMapper;

    @Override
    public CatalogWebDto getCatalog() {
        return catalogWebMapper.toCatalog(marketCatalogService.getCatalog());
    }
}
