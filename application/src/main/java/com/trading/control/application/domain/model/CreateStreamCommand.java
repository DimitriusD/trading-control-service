package com.trading.control.application.domain.model;

import com.trading.control.application.domain.model.stream.chanel.StreamChannelConfig;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CreateStreamCommand {
    String exchange;
    String marketType;
    CreateStreamInstrument instrument;
    @Singular
    List<StreamChannelConfig> channels;
    boolean startImmediately;
    boolean autoRestart;
}
