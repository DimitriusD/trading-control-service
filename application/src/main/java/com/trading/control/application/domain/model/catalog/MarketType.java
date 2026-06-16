package com.trading.control.application.domain.model.catalog;

import lombok.Builder;
import lombok.Value;

/**
 * Persistence model for the {@code market_types} table.
 */
@Value
@Builder
public class MarketType {
    String code;
    String name;
}
