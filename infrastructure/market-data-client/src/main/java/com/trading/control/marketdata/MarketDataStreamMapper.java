package com.trading.control.marketdata;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import com.trading.control.application.domain.model.instrument.InstrumentId;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.mds.client.model.ChannelDefinitionDto;
import com.trading.mds.client.model.ChannelTypeDto;
import com.trading.mds.client.model.CreateStreamRequestDto;
import com.trading.mds.client.model.ExchangeDto;
import com.trading.mds.client.model.InstrumentIdDto;
import com.trading.mds.client.model.MarketTypeDto;
import com.trading.mds.client.model.StreamResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface MarketDataStreamMapper {


    @Mapping(target = "instrument", source = "instrumentId")
    @Mapping(target = "channels", source = "channels")
    @Mapping(target = "enabled", source = "desired", qualifiedByName = "desiredToEnabled")
    CreateStreamRequestDto toCreateRequest(StreamDefinition definition);

    @Mapping(target = "exchange", source = "exchangeCode")
    @Mapping(target = "market", source = "marketCode")
    @Mapping(target = "base", source = "baseAssetCode")
    @Mapping(target = "quote", source = "quoteAssetCode")
    InstrumentIdDto toInstrumentIdDto(InstrumentId id);

    @Mapping(target = "type", source = "code")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "params", source = "params")
    ChannelDefinitionDto toChannelDefinition(Channel channel);

    @Mapping(target = "instrumentId", source = "instrument")
    @Mapping(target = "desired", source = "enabled", qualifiedByName = "enabledToDesired")
    @Mapping(target = "runtime", source = "enabled", qualifiedByName = "enabledToRuntime")
    @Mapping(target = "health", constant = "UNKNOWN")
    @Mapping(target = "channel", ignore = true)
    StreamDefinition toStreamDefinition(StreamResponseDto dto);

    @Mapping(target = "exchangeCode", source = "exchange")
    @Mapping(target = "marketCode", source = "market")
    @Mapping(target = "baseAssetCode", source = "base")
    @Mapping(target = "quoteAssetCode", source = "quote")
    InstrumentId toInstrumentId(InstrumentIdDto dto);

    @Mapping(target = "code", source = "type")
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "param", ignore = true)
    @Mapping(target = "params", source = "params")
    Channel toChannel(ChannelDefinitionDto dto);


    default ExchangeDto toExchangeDto(String code) {
        return code == null ? null : ExchangeDto.fromValue(code);
    }

    default MarketTypeDto toMarketTypeDto(String code) {
        return code == null ? null : MarketTypeDto.fromValue(code);
    }

    default ChannelTypeDto toChannelTypeDto(String code) {
        return code == null ? null : ChannelTypeDto.fromValue(code);
    }

    default String fromExchange(ExchangeDto exchange) {
        return exchange == null ? null : exchange.getValue();
    }

    default String fromMarketType(MarketTypeDto market) {
        return market == null ? null : market.getValue();
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

    @Named("desiredToEnabled")
    default Boolean desiredToEnabled(StreamDesiredState desired) {
        return desired == StreamDesiredState.ENABLED;
    }

    @Named("enabledToDesired")
    default StreamDesiredState enabledToDesired(Boolean enabled) {
        return Boolean.TRUE.equals(enabled) ? StreamDesiredState.ENABLED : StreamDesiredState.DISABLED;
    }

    @Named("enabledToRuntime")
    default StreamRuntimeState enabledToRuntime(Boolean enabled) {
        return Boolean.TRUE.equals(enabled) ? StreamRuntimeState.RUNNING : StreamRuntimeState.STOPPED;
    }

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
