package com.trading.control.application.domain.model;

import com.trading.control.application.domain.model.enums.MarketDataChannelType;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class StreamChannelOption {
    MarketDataChannelType type;
    String displayName;
    boolean enabled;
    boolean enabledByDefault;
    @Singular
    List<ChannelParamDefinition> params;
}
