package com.trading.control.application.domain.model.catalog;

import java.util.List;

public record CatalogExchange(String code, String displayName, List<CatalogMarket> markets) {
}
