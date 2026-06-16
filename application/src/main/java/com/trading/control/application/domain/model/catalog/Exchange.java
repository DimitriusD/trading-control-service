package com.trading.control.application.domain.model.catalog;

import lombok.Builder;
import lombok.Value;

/**
 * Persistence model for the {@code exchanges} table.
 */
@Value
@Builder
public class Exchange {
    String code;
    String name;
    boolean enabled;
}
