package com.trading.control.application.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChannelParamAllowedValue {
    String value;
    String displayName;
}
