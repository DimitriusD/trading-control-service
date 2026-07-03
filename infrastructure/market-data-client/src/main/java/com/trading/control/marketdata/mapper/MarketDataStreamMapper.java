package com.trading.control.marketdata.mapper;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.instrument.StreamInstrument;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;
import com.trading.mds.client.model.ChannelDefinitionDto;
import com.trading.mds.client.model.ChannelTypeDto;
import com.trading.mds.client.model.CreateStreamRequestDto;
import com.trading.mds.client.model.InstrumentDto;
import com.trading.mds.client.model.StreamResponseDto;
import com.trading.mds.client.model.StreamStateDto;
import com.trading.mds.client.model.UpdateStreamRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface MarketDataStreamMapper {

    // ===== outbound: control StreamDefinition -> MDS CreateStreamRequest =====

    @Mapping(target = "instrument", source = "instrument")
    CreateStreamRequestDto toCreateRequest(StreamDefinition definition);

    InstrumentDto toInstrumentDto(StreamInstrument instrument);

    @Mapping(target = "type", source = "code")
    @Mapping(target = "params", source = "params")
    ChannelDefinitionDto toChannelDefinition(Channel channel);

    default UpdateStreamRequestDto toUpdateRequest(StreamPatch patch) {
        UpdateStreamRequestDto dto = new UpdateStreamRequestDto();
        dto.setDesiredState(toStreamStateDto(patch.getDesiredState()));
        if (patch.getChannels() == null || patch.getChannels().isEmpty()) {
            dto.setChannels(null);
        } else {
            dto.setChannels(patch.getChannels().stream()
                    .map(this::toChannelDefinition)
                    .toList());
        }
        return dto;
    }

    default StreamStateDto toStreamStateDto(StreamDesiredState state) {
        return state == null ? null : StreamStateDto.fromValue(state.name());
    }

    // ===== inbound: MDS StreamResponse -> control StreamDefinition =====

    @Mapping(target = "instrument", source = "instrument")
    @Mapping(target = "runtime", source = "status.runtime")
    @Mapping(target = "health", source = "status.health")
    @Mapping(target = "channel", ignore = true)
    StreamDefinition toStreamDefinition(StreamResponseDto dto);

    StreamInstrument toStreamInstrument(InstrumentDto dto);

    @Mapping(target = "code", source = "type")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "param", ignore = true)
    Channel toChannel(ChannelDefinitionDto dto);

    default ChannelTypeDto toChannelTypeDto(String code) {
        return code == null ? null : ChannelTypeDto.fromValue(code);
    }

    default String fromChannelType(ChannelTypeDto type) {
        return type == null ? null : type.getValue();
    }

    default Map<String, String> toParamMap(List<ChannelParam> params) {
        if (params == null) {
            return Map.of();
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (ChannelParam param : params) {
            map.put(param.getKey(), selectedValue(param.getValues()));
        }
        return map;
    }

    default List<ChannelParam> toChannelParams(Map<String, String> params) {
        if (params == null) {
            return List.of();
        }
        return params.entrySet().stream()
                .map(entry -> ChannelParam.builder()
                        .key(entry.getKey())
                        .value(ChannelParamValue.builder().value(entry.getValue()).build())
                        .build())
                .toList();
    }

    default String selectedValue(List<ChannelParamValue> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .filter(ChannelParamValue::isDefault)
                .map(ChannelParamValue::getValue)
                .findFirst()
                .orElseGet(() -> values.getFirst().getValue());
    }
}
