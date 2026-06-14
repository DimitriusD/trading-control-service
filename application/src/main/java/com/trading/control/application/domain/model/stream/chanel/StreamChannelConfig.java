package com.trading.control.application.domain.model.stream.chanel;

import com.trading.control.application.domain.model.enums.MarketDataChannelType;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class StreamChannelConfig {
    MarketDataChannelType type;
    @Singular
    Map<String, String> params;
}
