package com.trading.control.application.domain.model.catalog;

import lombok.Builder;
import lombok.Value;

/**
 * Persistence model for the {@code exchange_markets} join table:
 * which market type is offered by an exchange, and whether it is enabled.
 */
@Value
@Builder
public class ExchangeMarketLink {
    String exchangeCode;
    String marketTypeCode;
    boolean enabled;
}
