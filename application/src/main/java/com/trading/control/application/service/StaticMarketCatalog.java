package com.trading.control.application.service;

import com.trading.control.application.domain.model.*;
import com.trading.control.application.domain.model.enums.ChannelParamType;
import com.trading.control.application.domain.model.enums.MarketDataChannelType;

import java.util.List;
import java.util.Map;

public final class StaticMarketCatalog {

    private static final List<InstrumentOption> BINANCE_SPOT_INSTRUMENTS = List.of(
            InstrumentOption.builder()
                    .symbol("BTCUSDT")
                    .baseAsset("BTC")
                    .baseAssetDisplayName("Bitcoin")
                    .quoteAsset("USDT")
                    .quoteAssetDisplayName("Tether")
                    .displaySymbol("BTC/USDT")
                    .enabled(true)
                    .build(),
            InstrumentOption.builder()
                    .symbol("ETHUSDT")
                    .baseAsset("ETH")
                    .baseAssetDisplayName("Ethereum")
                    .quoteAsset("USDT")
                    .quoteAssetDisplayName("Tether")
                    .displaySymbol("ETH/USDT")
                    .enabled(true)
                    .build(),
            InstrumentOption.builder()
                    .symbol("SOLUSDT")
                    .baseAsset("SOL")
                    .baseAssetDisplayName("Solana")
                    .quoteAsset("USDT")
                    .quoteAssetDisplayName("Tether")
                    .displaySymbol("SOL/USDT")
                    .enabled(true)
                    .build()
    );

    private static final StreamChannelOption TRADE_CHANNEL = StreamChannelOption.builder()
            .type(MarketDataChannelType.TRADE)
            .displayName("Trades")
            .enabled(true)
            .enabledByDefault(true)
            .build();

    private static final StreamChannelOption DEPTH_DIFF_CHANNEL = StreamChannelOption.builder()
            .type(MarketDataChannelType.DEPTH_DIFF)
            .displayName("Depth Diff")
            .enabled(true)
            .enabledByDefault(true)
            .param(ChannelParamDefinition.builder()
                    .name("updateSpeed")
                    .displayName("Update speed")
                    .type(ChannelParamType.SELECT)
                    .required(true)
                    .defaultValue("100ms")
                    .allowedValue(ChannelParamAllowedValue.builder()
                            .value("100ms")
                            .displayName("100 ms")
                            .build())
                    .allowedValue(ChannelParamAllowedValue.builder()
                            .value("1000ms")
                            .displayName("1000 ms")
                            .build())
                    .build())
            .build();

    private static final MarketTypeDescriptor BINANCE_SPOT = MarketTypeDescriptor.builder()
            .code("SPOT")
            .displayName("Spot")
            .enabled(true)
            .streamChannel(TRADE_CHANNEL)
            .streamChannel(DEPTH_DIFF_CHANNEL)
            .build();


    private static final MarketTypeDescriptor BINANCE_OPTION = MarketTypeDescriptor.builder()
            .code("OPTION")
            .displayName("Option")
            .enabled(true)
            .streamChannel(TRADE_CHANNEL)
            .streamChannel(DEPTH_DIFF_CHANNEL)
            .build();

    private static final ExchangeMarket BINANCE = ExchangeMarket.builder()
            .code("BINANCE")
            .displayName("Binance")
            .enabled(true)
            .marketTypes(List.of(BINANCE_OPTION, BINANCE_SPOT))
            .build();

    private static final MarketCatalog CATALOG = MarketCatalog.builder()
            .exchange(BINANCE)
            .build();

    private static final Map<String, Map<String, List<InstrumentOption>>> INSTRUMENTS = Map.of(
            "BINANCE", Map.of("SPOT", BINANCE_SPOT_INSTRUMENTS)
    );

    private StaticMarketCatalog() {
    }

    public static MarketCatalog getCatalog() {
        return CATALOG;
    }

    public static boolean hasExchange(String exchange) {
        return INSTRUMENTS.containsKey(exchange);
    }

    public static boolean hasMarketType(String exchange, String marketType) {
        var marketTypes = INSTRUMENTS.get(exchange);
        return marketTypes != null && marketTypes.containsKey(marketType);
    }

    public static List<InstrumentOption> getInstruments(String exchange, String marketType) {
        return INSTRUMENTS.get(exchange).get(marketType);
    }
}
