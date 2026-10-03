package com.trading.control.application.service;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.model.Asset;
import com.trading.control.application.domain.model.catalog.Catalog;
import com.trading.control.application.domain.model.catalog.ChannelCapability;
import com.trading.control.application.domain.model.catalog.ChannelParamCapability;
import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.instrument.Instrument;
import com.trading.control.application.domain.model.instrument.InstrumentPage;
import com.trading.control.application.domain.model.instrument.InstrumentSearchQuery;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;
import com.trading.control.application.port.output.MarketCatalogPort;
import com.trading.control.application.port.output.MarketDataStreamControlPort;

import java.util.List;

/**
 * Shared test data and configurable fakes for the stream service unit tests.
 * BINANCE|SPOT|BTC|USDT with TRADE + DEPTH_DIFF(updateSpeed: 100ms|1000ms).
 */
final class CatalogFixtures {

    static final String INSTRUMENT_ID = "BINANCE|SPOT|BTC|USDT";

    private CatalogFixtures() {
    }

    static Instrument.InstrumentBuilder tradingInstrument() {
        return Instrument.builder()
                .instrumentId(INSTRUMENT_ID)
                .exchangeCode("BINANCE")
                .marketCode("SPOT")
                .baseAsset(Asset.builder().code("BTC").name("Bitcoin").build())
                .quoteAsset(Asset.builder().code("USDT").name("Tether USD").build())
                .exchangeSymbol("BTCUSDT")
                .displaySymbol("BTC/USDT")
                .enabled(true);
    }

    static List<ChannelCapability> binanceSpotCapabilities() {
        return List.of(
                ChannelCapability.builder().code("TRADE").enabled(true).build(),
                ChannelCapability.builder().code("DEPTH_DIFF").enabled(true)
                        .param(ChannelParamCapability.builder()
                                .key("updateSpeed").required(true)
                                .allowedValue("100ms").allowedValue("1000ms")
                                .build())
                        .build());
    }

    static Channel channel(String code) {
        return Channel.builder().code(code).enabled(true).build();
    }

    static Channel depthDiff(String updateSpeed) {
        Channel.ChannelBuilder builder = Channel.builder().code("DEPTH_DIFF").enabled(true);
        if (updateSpeed != null) {
            builder.param(ChannelParam.builder()
                    .key("updateSpeed")
                    .value(ChannelParamValue.builder().value(updateSpeed).build())
                    .build());
        }
        return builder.build();
    }

    /** Configurable market catalog: one instrument + a fixed capability set. */
    static final class FakeMarketCatalog implements MarketCatalogPort {
        Instrument instrument;
        List<ChannelCapability> capabilities = List.of();

        @Override
        public Catalog getCatalog() {
            return new Catalog(List.of());
        }

        @Override
        public InstrumentPage searchInstruments(InstrumentSearchQuery query) {
            return new InstrumentPage(instrument == null ? List.of() : List.of(instrument), null);
        }

        @Override
        public Instrument getInstrument(String instrumentId) {
            if (instrument == null || !instrument.getInstrumentId().equals(instrumentId)) {
                throw new NotFoundException("Instrument not found: " + instrumentId);
            }
            return instrument;
        }

        @Override
        public List<ChannelCapability> getChannelCapabilities(String exchangeCode, String marketCode) {
            return capabilities;
        }
    }

    /** Captures the definition sent to MDS and echoes a canned response. */
    static final class CapturingControlPort implements MarketDataStreamControlPort {
        StreamDefinition createdDefinition;
        StreamPatch updatedPatch;
        StreamDefinition getStreamResponse;
        StreamDefinition response;

        @Override
        public List<StreamDefinition> listStreams() {
            return List.of();
        }

        @Override
        public StreamDefinition getStream(String streamId) {
            return getStreamResponse;
        }

        @Override
        public StreamDefinition createStream(StreamDefinition command) {
            this.createdDefinition = command;
            return response != null ? response : command;
        }

        @Override
        public StreamDefinition updateStream(String streamId, StreamPatch patch) {
            this.updatedPatch = patch;
            return response != null ? response : getStreamResponse;
        }

        @Override
        public void deleteStream(String streamId) {
        }
    }
}
