package com.trading.control.application.service;

import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.catalog.ChannelParamCapability;
import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.port.input.MarketCatalogService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamCommandValidator {

    private final MarketCatalogService marketCatalogService;

    public StreamCommandValidator(MarketCatalogService marketCatalogService) {
        this.marketCatalogService = marketCatalogService;
    }

    public void validateCreate(Instrument instrument, List<Channel> channels) {
        List<String> errors = new ArrayList<>();

        if (!instrument.isEnabled()) {
            errors.add("instrument is not enabled: " + instrument.getInstrumentId());
        }

        validateChannelsInto(instrument.getExchangeCode(), instrument.getMarketCode(), channels, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    /**
     * Validates a channel set against an existing stream's exchange/market — used
     * for PATCH, where the instrument is already fixed.
     */
    public void validateChannels(String exchangeCode, String marketCode, List<Channel> channels) {
        List<String> errors = new ArrayList<>();
        validateChannelsInto(exchangeCode, marketCode, channels, errors);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateChannelsInto(String exchangeCode, String marketCode,
                                      List<Channel> channels, List<String> errors) {
        if (channels == null || channels.isEmpty()) {
            errors.add("channels must not be empty");
            return;
        }

        Map<String, ChannelCapability> capabilities = marketCatalogService
                .getChannelCapabilities(exchangeCode, marketCode).stream()
                .collect(Collectors.toMap(ChannelCapability::getCode, Function.identity(), (a, b) -> a));

        Set<String> seen = new HashSet<>();
        for (Channel channel : channels) {
            String code = channel.getCode();
            if (code == null || code.isBlank()) {
                errors.add("channel code must not be blank");
                continue;
            }
            if (!seen.add(code)) {
                errors.add("duplicate channel: " + code);
                continue;
            }
            ChannelCapability capability = capabilities.get(code);
            if (capability == null) {
                errors.add("unsupported channel for " + exchangeCode + "/" + marketCode + ": " + code);
                continue;
            }
            if (!capability.isEnabled()) {
                errors.add("channel is not enabled: " + code);
                continue;
            }
            validateParams(code, channel, capability, errors);
        }
    }

    private void validateParams(String code, Channel channel, ChannelCapability capability, List<String> errors) {
        Map<String, ChannelParamCapability> paramCaps = capability.getParams().stream()
                .collect(Collectors.toMap(ChannelParamCapability::getKey, Function.identity(), (a, b) -> a));

        Map<String, String> provided = new LinkedHashMap<>();
        for (ChannelParam param : channel.getParams()) {
            provided.put(param.getKey(), selectedValue(param));
        }

        // Reject params the channel does not know about.
        for (String key : provided.keySet()) {
            if (!paramCaps.containsKey(key)) {
                errors.add("unknown param '" + key + "' for channel " + code);
            }
        }

        // Required params must be present; provided values must be allowed.
        for (ChannelParamCapability paramCap : capability.getParams()) {
            String value = provided.get(paramCap.getKey());
            if (paramCap.isRequired() && (value == null || value.isBlank())) {
                errors.add("missing required param '" + paramCap.getKey() + "' for channel " + code);
                continue;
            }
            if (value != null && !paramCap.getAllowedValues().isEmpty()
                    && !paramCap.getAllowedValues().contains(value)) {
                errors.add("invalid value '" + value + "' for param '" + paramCap.getKey()
                        + "' of channel " + code + "; allowed: " + paramCap.getAllowedValues());
            }
        }
    }

    private static String selectedValue(ChannelParam param) {
        List<ChannelParamValue> values = param.getValues();
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0).getValue();
    }
}
