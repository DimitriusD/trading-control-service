package com.trading.control.application.domain.model.instrument;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StreamInstrument {
    String instrumentId;
    String exchangeCode;
    String marketCode;
    String baseAssetCode;
    String quoteAssetCode;
    String exchangeSymbol;
}
