package com.trading.control.mapper;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.instrument.StreamInstrument;
import com.trading.control.application.domain.model.stream.CreateStreamCommand;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;
import com.trading.control.restapi.generated.model.ChannelParamSelectionWebDto;
import com.trading.control.restapi.generated.model.ChannelSelectionWebDto;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import com.trading.control.restapi.generated.model.StreamInstrumentWebDto;
import com.trading.control.restapi.generated.model.StreamResponseWebDto;
import com.trading.control.restapi.generated.model.UpdateStreamRequestWebDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StreamWebMapper {

    CreateStreamCommand toCreateCommand(CreateStreamRequestWebDto request);

    StreamPatch toStreamPatch(UpdateStreamRequestWebDto request);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "param", ignore = true)
    Channel toChannel(ChannelSelectionWebDto dto);

    @Mapping(target = "value", ignore = true)
    @Mapping(target = "values", expression = "java(wrapValue(dto.getValue()))")
    ChannelParam toChannelParam(ChannelParamSelectionWebDto dto);

    default List<ChannelParamValue> wrapValue(String value) {
        return value == null ? List.of() : List.of(ChannelParamValue.builder().value(value).build());
    }

    @Mapping(target = "instrument", source = "instrument")
    @Mapping(target = "desiredState", source = "desiredState")
    @Mapping(target = "status.runtime", source = "runtime")
    @Mapping(target = "status.health", source = "health")
    StreamResponseWebDto toStreamResponse(StreamDefinition stream);

    StreamInstrumentWebDto toStreamInstrument(StreamInstrument instrument);

    ChannelSelectionWebDto toChannelSelection(Channel channel);

    @Mapping(target = "value", expression = "java(selectedValue(param.getValues()))")
    ChannelParamSelectionWebDto toChannelParamSelection(ChannelParam param);

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
