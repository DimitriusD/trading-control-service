package com.trading.control.application.domain.model.catalog;

import java.util.List;

public record CatalogChannel(String code, String name, List<CatalogChannelParam> params) {
}
