package com.trading.control.application.domain.model.instrument;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InstrumentId {
    String exchangeCode;
    String marketCode;
    String baseAssetCode;
    String quoteAssetCode;
}
