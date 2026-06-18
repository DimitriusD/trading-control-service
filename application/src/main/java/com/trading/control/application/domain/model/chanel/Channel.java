package com.trading.control.application.domain.model.chanel;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Channel {
    String code;
    String name;
    boolean enabled;
    @Singular
    List<ChannelParam> params;
}
