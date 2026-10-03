package com.trading.control.application.domain.model.catalog;

import java.util.List;

public record CatalogChannelParam(String key, boolean required, String defaultValue, List<CatalogParamValue> values) {
}
