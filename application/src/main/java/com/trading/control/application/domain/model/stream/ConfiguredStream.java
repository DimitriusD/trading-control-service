package com.trading.control.application.domain.model.stream;

import com.trading.control.application.domain.model.stream.chanel.StreamChannelConfig;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamHealthState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class ConfiguredStream {
    String id;
    String instrumentId;
    String instrumentKey;
    String pair;
    String symbol;
    String exchange;
    String market;
    String baseAsset;
    String quoteAsset;
    @Singular
    List<StreamChannelConfig> channels;
    StreamDesiredState desired;
    StreamRuntimeState runtime;
    StreamHealthState health;
    boolean autoRestart;
}
