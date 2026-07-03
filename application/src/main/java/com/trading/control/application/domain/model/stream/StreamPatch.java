package com.trading.control.application.domain.model.stream;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class StreamPatch {
    StreamDesiredState desiredState;
    List<Channel> channels;
}
