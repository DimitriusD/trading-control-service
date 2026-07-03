package com.trading.control.application.service;

import com.trading.control.application.domain.exception.NotFoundException;
import com.trading.control.application.domain.exception.ValidationException;
import com.trading.control.application.domain.model.instrument.Instrument;
import org.junit.jupiter.api.Test;

import static com.trading.control.application.service.CatalogFixtures.INSTRUMENT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MarketCatalogServiceImplTest {

    private final CatalogFixtures.FakeCatalogStore catalog = new CatalogFixtures.FakeCatalogStore();
    private final MarketCatalogServiceImpl service = new MarketCatalogServiceImpl(catalog);

    @Test
    void resolvesCanonicalInstrumentIdIntoFullDetails() {
        catalog.instrument = CatalogFixtures.tradingInstrument().build();

        Instrument details = service.getInstrument(INSTRUMENT_ID);

        assertEquals(INSTRUMENT_ID, details.getInstrumentId());
        assertEquals("BINANCE", details.getExchangeCode());
        assertEquals("SPOT", details.getMarketCode());
        assertEquals("BTC", details.getBaseAsset().getCode());
        assertEquals("USDT", details.getQuoteAsset().getCode());
        assertEquals("BTCUSDT", details.getExchangeSymbol());
    }

    @Test
    void throwsNotFoundForUnknownInstrument() {
        catalog.instrument = null;
        assertThrows(NotFoundException.class, () -> service.getInstrument(INSTRUMENT_ID));
    }

    @Test
    void throwsValidationForBlankInstrumentId() {
        assertThrows(ValidationException.class, () -> service.getInstrument("  "));
        assertThrows(ValidationException.class, () -> service.getInstrument(null));
    }
}
