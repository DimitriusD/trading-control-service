package com.trading.control.application.domain.model;

import com.trading.control.application.domain.model.chanel.Channel;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class MarketType {
    String code;
    String displayName;
    boolean enabled;
    @Singular
    List<Channel> channels;
}
