package com.trading.control.application.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InstrumentOption {
    String symbol;
    String baseAsset;
    String baseAssetDisplayName;
    String quoteAsset;
    String quoteAssetDisplayName;
    String displaySymbol;
    boolean enabled;
}
