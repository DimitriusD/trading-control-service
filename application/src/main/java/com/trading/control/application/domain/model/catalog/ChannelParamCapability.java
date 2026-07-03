package com.trading.control.application.domain.model.catalog;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChannelParamCapability {
    String key;
    boolean required;
    @Singular
    List<String> allowedValues;
}
