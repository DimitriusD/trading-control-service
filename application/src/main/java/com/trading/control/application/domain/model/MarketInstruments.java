package com.trading.control.application.domain.model;

import com.trading.control.application.domain.model.instrument.Instrument;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MarketInstruments {
    String exchange;
    String marketType;
    @Singular
    List<Instrument> instruments;
}
