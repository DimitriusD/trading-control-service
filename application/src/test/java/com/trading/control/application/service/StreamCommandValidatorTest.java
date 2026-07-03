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

    private final CatalogFixtures.FakeCatalogStore catalog = new CatalogFixtures.FakeCatalogStore();
    private final StreamCommandValidator validator =
            new StreamCommandValidator(new MarketCatalogServiceImpl(catalog));

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
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(
                instrument().enabled(false).build(),
                List.of(CatalogFixtures.channel("TRADE"))));
        assertTrue(ex.getMessage().contains("not enabled"));
    }

    @Test
    void rejectsEmptyChannels() {
        assertThrows(ValidationException.class, () -> validator.validateCreate(
                instrument().build(), List.of()));
    }

    @Test
    void rejectsDuplicateChannels() {
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(
                instrument().build(),
                List.of(CatalogFixtures.channel("TRADE"), CatalogFixtures.channel("TRADE"))));
        assertTrue(ex.getMessage().contains("duplicate"));
    }

    @Test
    void rejectsUnsupportedChannel() {
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(
                instrument().build(),
                List.of(CatalogFixtures.channel("KLINE"))));
        assertTrue(ex.getMessage().contains("unsupported"));
    }

    @Test
    void rejectsMissingRequiredUpdateSpeed() {
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(
                instrument().build(),
                List.of(CatalogFixtures.depthDiff(null))));
        assertTrue(ex.getMessage().contains("updateSpeed"));
    }

    @Test
    void rejectsInvalidUpdateSpeed() {
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(
                instrument().build(),
                List.of(CatalogFixtures.depthDiff("500ms"))));
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
        var ex = assertThrows(ValidationException.class, () -> validator.validateCreate(
                instrument().build(), List.of(tradeWithBogusParam)));
        assertTrue(ex.getMessage().contains("unknown param"));
    }

    @Test
    void validateChannelsUsesGivenExchangeMarket() {
        assertDoesNotThrow(() -> validator.validateChannels(
                "BINANCE", "SPOT", List.of(CatalogFixtures.depthDiff("1000ms"))));
    }
}
