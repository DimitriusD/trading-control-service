package com.trading.control.application.service;

import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.instrument.StreamInstrument;
import com.trading.control.application.domain.model.stream.CreateStreamCommand;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;
import com.trading.control.application.port.input.StreamService;
import com.trading.control.application.port.output.MarketCatalogPort;
import com.trading.control.application.port.output.MarketDataStreamControlPort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class StreamServiceImpl implements StreamService {

    private final MarketDataStreamControlPort marketDataStreamControlPort;
    private final MarketCatalogPort marketCatalogPort;
    private final StreamCommandValidator commandValidator;

    @Override
    public List<StreamDefinition> getStreams() {
        return marketDataStreamControlPort.listStreams();
    }

    @Override
    public StreamDefinition getStream(String streamId) {
        return marketDataStreamControlPort.getStream(streamId);
    }

    @Override
    public StreamDefinition createStream(CreateStreamCommand command) {
        String instrumentId = command.getInstrumentId();
        if (instrumentId == null || instrumentId.isBlank()) {
            throw new ValidationException("instrumentId must not be blank");
        }
        Instrument instrument = marketCatalogPort.getInstrument(instrumentId);
        commandValidator.validateCreate(instrument, command.getChannels());

        StreamDefinition definition = StreamDefinition.builder()
                .instrument(toStreamInstrument(instrument))
                .desiredState(command.getDesiredState())
                .channels(command.getChannels())
                .build();

        return marketDataStreamControlPort.createStream(definition);
    }

    @Override
    public StreamDefinition updateStream(String streamId, StreamPatch patch) {
        // desiredState-only patches need no catalog lookup; channel changes are
        // validated against the stream's existing (already-resolved) instrument.
        if (patch.getChannels() != null && !patch.getChannels().isEmpty()) {
            StreamDefinition current = marketDataStreamControlPort.getStream(streamId);
            StreamInstrument instrument = current.getInstrument();
            commandValidator.validateChannels(
                    instrument.getExchangeCode(), instrument.getMarketCode(), patch.getChannels());
        }
        return marketDataStreamControlPort.updateStream(streamId, patch);
    }

    @Override
    public void deleteStream(String streamId) {
        marketDataStreamControlPort.deleteStream(streamId);
    }

    private static StreamInstrument toStreamInstrument(Instrument instrument) {
        return StreamInstrument.builder()
                .instrumentId(instrument.getInstrumentId())
                .exchangeCode(instrument.getExchangeCode())
                .marketCode(instrument.getMarketCode())
                .baseAssetCode(instrument.getBaseAsset() == null ? null : instrument.getBaseAsset().getCode())
                .quoteAssetCode(instrument.getQuoteAsset() == null ? null : instrument.getQuoteAsset().getCode())
                .exchangeSymbol(instrument.getExchangeSymbol())
                .build();
    }
}
