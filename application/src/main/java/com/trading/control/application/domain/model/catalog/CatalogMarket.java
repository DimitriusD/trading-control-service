package com.trading.control.application.domain.model.catalog;

import java.util.List;

public record CatalogMarket(String code, String marketType, String displayName, List<CatalogChannel> channels) {
}
