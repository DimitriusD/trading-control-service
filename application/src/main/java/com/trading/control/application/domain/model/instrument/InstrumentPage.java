package com.trading.control.application.domain.model.instrument;

import java.util.List;

public record InstrumentPage(List<Instrument> items, String nextCursor) {
}
