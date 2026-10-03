package com.trading.control.application.domain.model.instrument;

public record InstrumentSearchQuery(String exchangeCode,
                                    String marketCode,
                                    String searchText,
                                    String baseAssetCode,
                                    String quoteAssetCode,
                                    Integer limit,
                                    String cursor) {
}
