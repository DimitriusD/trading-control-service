package com.trading.control.application.service;

import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.instrument.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamCommandValidatorTest {

    private final CatalogFixtures.FakeMarketCatalog catalog = new CatalogFixtures.FakeMarketCatalog();
    private final StreamCommandValidator validator =
            new StreamCommandValidator(catalog);

    @BeforeEach
    void setUp() {
        catalog.capabilities = CatalogFixtures.binanceSpotCapabilities();
    }

    private Instrument.InstrumentBuilder instrument() {
        return CatalogFixtures.tradingInstrument();
    }

    @Test
    void acceptsValidCommand() {
        assertDoesNotThrow(() -> validator.validateCreate(
                instrument().build(),
                List.of(CatalogFixtures.channel("TRADE"), CatalogFixtures.depthDiff("100ms"))));
    }

    @Test
    void rejectsDisabledInstrument() {
        var instrument = instrument().enabled(false).build();
        var channels = List.of(CatalogFixtures.channel("TRADE"));
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(instrument, channels));
        assertTrue(ex.getMessage().contains("not enabled"));
    }

    @Test
    void rejectsEmptyChannels() {
        var instrument = instrument().build();
        List<Channel> channels = List.of();
        assertThrows(ValidationException.class, () -> validator.validateCreate(instrument, channels));
    }

    @Test
    void rejectsDuplicateChannels() {
        var instrument = instrument().build();
        var channels = List.of(CatalogFixtures.channel("TRADE"), CatalogFixtures.channel("TRADE"));
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(instrument, channels));
        assertTrue(ex.getMessage().contains("duplicate"));
    }

    @Test
    void rejectsUnsupportedChannel() {
        var instrument = instrument().build();
        var channels = List.of(CatalogFixtures.channel("KLINE"));
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(instrument, channels));
        assertTrue(ex.getMessage().contains("unsupported"));
    }

    @Test
    void rejectsMissingRequiredUpdateSpeed() {
        var instrument = instrument().build();
        var channels = List.of(CatalogFixtures.depthDiff(null));
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(instrument, channels));
        assertTrue(ex.getMessage().contains("updateSpeed"));
    }

    @Test
    void rejectsInvalidUpdateSpeed() {
        var instrument = instrument().build();
        var channels = List.of(CatalogFixtures.depthDiff("500ms"));
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(instrument, channels));
        assertTrue(ex.getMessage().contains("invalid value"));
    }

    @Test
    void rejectsUnknownParam() {
        Channel tradeWithBogusParam = Channel.builder().code("TRADE").enabled(true)
                .param(com.trading.control.application.domain.model.chanel.ChannelParam.builder()
                        .key("bogus")
                        .value(com.trading.control.application.domain.model.chanel.ChannelParamValue.builder()
                                .value("x").build())
                        .build())
                .build();
        var instrument = instrument().build();
        var channels = List.of(tradeWithBogusParam);
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(instrument, channels));
        assertTrue(ex.getMessage().contains("unknown param"));
    }

    @Test
    void validateChannelsUsesGivenExchangeMarket() {
        assertDoesNotThrow(() -> validator.validateChannels(
                "BINANCE", "SPOT", List.of(CatalogFixtures.depthDiff("1000ms"))));
    }
}
