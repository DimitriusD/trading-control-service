package com.trading.control.application.domain.model.stream;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamHealthState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import com.trading.control.application.domain.model.instrument.StreamInstrument;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class StreamDefinition {
    String streamId;
    StreamInstrument instrument;
    @Singular
    List<Channel> channels;
    StreamDesiredState desiredState;
    StreamRuntimeState runtime;
    StreamHealthState health;
}
