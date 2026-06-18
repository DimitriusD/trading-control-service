package com.trading.control.rest.mapper;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.instrument.InstrumentId;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.restapi.generated.model.ChannelParamSelectionWebDto;
import com.trading.control.restapi.generated.model.ChannelSelectionWebDto;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import com.trading.control.restapi.generated.model.InstrumentIdWebDto;
import com.trading.control.restapi.generated.model.StreamResponseWebDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StreamWebMapper {

    // ===== inbound: CreateStreamRequest -> StreamDefinition =====

    @Mapping(target = "streamId", ignore = true)
    @Mapping(target = "instrumentId", source = "instrument")
    @Mapping(target = "desired", source = "startImmediately", qualifiedByName = "desiredFromFlag")
    @Mapping(target = "runtime", ignore = true)
    @Mapping(target = "health", ignore = true)
    @Mapping(target = "channel", ignore = true)
    StreamDefinition toStreamDefinition(CreateStreamRequestWebDto request);

    InstrumentId toInstrumentId(InstrumentIdWebDto dto);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "param", ignore = true)
    Channel toChannel(ChannelSelectionWebDto dto);

    @Mapping(target = "value", ignore = true)
    @Mapping(target = "values", expression = "java(wrapValue(dto.getValue()))")
    ChannelParam toChannelParam(ChannelParamSelectionWebDto dto);

    @Named("desiredFromFlag")
    default StreamDesiredState desiredFromFlag(Boolean startImmediately) {
        return Boolean.TRUE.equals(startImmediately) ? StreamDesiredState.ENABLED : StreamDesiredState.DISABLED;
    }

    default List<ChannelParamValue> wrapValue(String value) {
        return value == null ? List.of() : List.of(ChannelParamValue.builder().value(value).build());
    }

    // ===== outbound: StreamDefinition -> StreamResponse =====

    @Mapping(target = "instrument", source = "instrumentId")
    StreamResponseWebDto toStreamResponse(StreamDefinition stream);

    InstrumentIdWebDto toInstrumentIdDto(InstrumentId id);

    ChannelSelectionWebDto toChannelSelection(Channel channel);

    @Mapping(target = "value", expression = "java(selectedValue(param.getValues()))")
    ChannelParamSelectionWebDto toChannelParamSelection(ChannelParam param);

    /**
     * Resolve a param's configured value: the one flagged default, else the first available.
     */
    default String selectedValue(List<ChannelParamValue> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .filter(ChannelParamValue::isDefault)
                .map(ChannelParamValue::getValue)
                .findFirst()
                .orElseGet(() -> values.get(0).getValue());
    }
}
