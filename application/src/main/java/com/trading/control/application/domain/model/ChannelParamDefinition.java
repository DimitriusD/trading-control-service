package com.trading.control.application.domain.model;

import com.trading.control.application.domain.model.enums.ChannelParamType;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChannelParamDefinition {
    String name;
    String displayName;
    ChannelParamType type;
    boolean required;
    String defaultValue;
    @Singular
    List<ChannelParamAllowedValue> allowedValues;
}
