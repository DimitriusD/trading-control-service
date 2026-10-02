package com.trading.control.application.service;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.instrument.StreamInstrument;
import com.trading.control.application.domain.model.stream.CreateStreamCommand;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.application.domain.model.stream.StreamPatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.trading.control.application.service.CatalogFixtures.INSTRUMENT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StreamServiceImplTest {

    private final CatalogFixtures.FakeMarketCatalog catalog = new CatalogFixtures.FakeMarketCatalog();
    private final CatalogFixtures.CapturingControlPort port = new CatalogFixtures.CapturingControlPort();
    private StreamServiceImpl service;

    @BeforeEach
    void setUp() {
        catalog.instrument = CatalogFixtures.tradingInstrument().build();
        catalog.capabilities = CatalogFixtures.binanceSpotCapabilities();
        service = new StreamServiceImpl(port, catalog, new StreamCommandValidator(catalog));
    }

    @Test
    void createResolvesInstrumentIdIntoFullInstrumentBeforeCallingMds() {
        CreateStreamCommand command = CreateStreamCommand.builder()
                .instrumentId(INSTRUMENT_ID)
                .desiredState(StreamDesiredState.ENABLED)
                .channels(List.of(CatalogFixtures.channel("TRADE"), CatalogFixtures.depthDiff("100ms")))
                .build();

        service.createStream(command);

        StreamInstrument sent = port.createdDefinition.getInstrument();
        assertEquals(INSTRUMENT_ID, sent.getInstrumentId());
        assertEquals("BINANCE", sent.getExchangeCode());
        assertEquals("SPOT", sent.getMarketCode());
        assertEquals("BTC", sent.getBaseAssetCode());
        assertEquals("USDT", sent.getQuoteAssetCode());
        assertEquals("BTCUSDT", sent.getExchangeSymbol());
        assertEquals(StreamDesiredState.ENABLED, port.createdDefinition.getDesiredState());
        assertEquals(2, port.createdDefinition.getChannels().size());
    }

    @Test
    void createRejectsUnknownInstrumentWithoutCallingMds() {
        catalog.instrument = null;
        CreateStreamCommand command = CreateStreamCommand.builder()
                .instrumentId(INSTRUMENT_ID)
                .desiredState(StreamDesiredState.ENABLED)
                .channels(List.of(CatalogFixtures.channel("TRADE")))
                .build();

        assertThrows(NotFoundException.class, () -> service.createStream(command));
        org.junit.jupiter.api.Assertions.assertEquals(null, port.createdDefinition);
    }

    @Test
    void createRejectsBlankInstrumentIdWithoutCallingCatalog() {
        catalog.instrument = null;
        CreateStreamCommand command = CreateStreamCommand.builder()
                .instrumentId("  ")
                .desiredState(StreamDesiredState.ENABLED)
                .channels(List.of(CatalogFixtures.channel("TRADE")))
                .build();

        assertThrows(com.trading.control.application.domain.exception.ValidationException.class,
                () -> service.createStream(command));
    }

    @Test
    void patchWithChannelsValidatesAgainstExistingStreamInstrument() {
        port.getStreamResponse = StreamDefinition.builder()
                .streamId("binance-spot-btc-usdt")
                .instrument(StreamInstrument.builder()
                        .instrumentId(INSTRUMENT_ID)
                        .exchangeCode("BINANCE").marketCode("SPOT")
                        .baseAssetCode("BTC").quoteAssetCode("USDT").exchangeSymbol("BTCUSDT")
                        .build())
                .build();

        StreamPatch patch = StreamPatch.builder()
                .channels(List.of(CatalogFixtures.depthDiff("100ms")))
                .build();

        service.updateStream("binance-spot-btc-usdt", patch);
        assertEquals(patch, port.updatedPatch);
    }

    @Test
    void patchWithInvalidChannelsIsRejected() {
        port.getStreamResponse = StreamDefinition.builder()
                .instrument(StreamInstrument.builder()
                        .instrumentId(INSTRUMENT_ID)
                        .exchangeCode("BINANCE").marketCode("SPOT")
                        .baseAssetCode("BTC").quoteAssetCode("USDT").exchangeSymbol("BTCUSDT")
                        .build())
                .build();

        StreamPatch patch = StreamPatch.builder()
                .channels(List.of(CatalogFixtures.depthDiff("500ms")))
                .build();

        assertThrows(com.trading.control.application.domain.exception.ValidationException.class,
                () -> service.updateStream("s", patch));
    }
}
