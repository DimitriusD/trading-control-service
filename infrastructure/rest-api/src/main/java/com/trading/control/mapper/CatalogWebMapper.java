package com.trading.control.mapper;

import com.trading.control.application.domain.model.Asset;
import com.trading.control.application.domain.model.catalog.Catalog;
import com.trading.control.application.domain.model.catalog.CatalogChannel;
import com.trading.control.application.domain.model.catalog.CatalogChannelParam;
import com.trading.control.application.domain.model.catalog.CatalogExchange;
import com.trading.control.application.domain.model.catalog.CatalogMarket;
import com.trading.control.application.domain.model.catalog.CatalogParamValue;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.instrument.InstrumentPage;
import com.trading.control.restapi.generated.model.AssetWebDto;
import com.trading.control.restapi.generated.model.CatalogChannelParamWebDto;
import com.trading.control.restapi.generated.model.CatalogChannelWebDto;
import com.trading.control.restapi.generated.model.CatalogExchangeWebDto;
import com.trading.control.restapi.generated.model.CatalogMarketWebDto;
import com.trading.control.restapi.generated.model.CatalogParamValueWebDto;
import com.trading.control.restapi.generated.model.CatalogWebDto;
import com.trading.control.restapi.generated.model.InstrumentPageWebDto;
import com.trading.control.restapi.generated.model.InstrumentWebDto;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring")
public interface CatalogWebMapper {

    CatalogWebDto toCatalog(Catalog catalog);

    CatalogExchangeWebDto toCatalogExchange(CatalogExchange exchange);

    CatalogMarketWebDto toCatalogMarket(CatalogMarket market);

    CatalogChannelWebDto toCatalogChannel(CatalogChannel channel);

    CatalogChannelParamWebDto toCatalogChannelParam(CatalogChannelParam param);

    CatalogParamValueWebDto toCatalogParamValue(CatalogParamValue value);

    InstrumentPageWebDto toInstrumentPage(InstrumentPage page);

    InstrumentWebDto toInstrument(Instrument instrument);

    AssetWebDto toAsset(Asset asset);

    default JsonNullable<String> nullable(String value) {
        return JsonNullable.of(value);
    }
}
