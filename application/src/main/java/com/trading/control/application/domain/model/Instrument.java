package com.trading.control.application.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Instrument {
    String instrumentId;
    Asset baseAsset;
    Asset quoteAsset;
    String displaySymbol;
    boolean enabled;
}
