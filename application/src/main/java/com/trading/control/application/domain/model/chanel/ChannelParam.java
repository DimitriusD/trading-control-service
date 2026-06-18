package com.trading.control.application.domain.model.chanel;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChannelParam {
    String key;
    @Singular
    List<ChannelParamValue> values;
}
