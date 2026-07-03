package com.trading.control.application.domain.model.catalog;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChannelCapability {
    String code;
    boolean enabled;
    @Singular
    List<ChannelParamCapability> params;
}
