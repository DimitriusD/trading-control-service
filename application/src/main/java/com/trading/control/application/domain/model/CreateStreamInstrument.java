package com.trading.control.application.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateStreamInstrument {
    String symbol;
    String baseAsset;
    String quoteAsset;
    String instrumentId;
}
