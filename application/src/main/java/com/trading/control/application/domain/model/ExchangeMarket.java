package com.trading.control.application.domain.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ExchangeMarket {
    String code;
    String displayName;
    boolean enabled;
    @Singular
    List<MarketTypeDescriptor> marketTypes;
}
