package com.trading.control.application.service;

import com.trading.control.application.domain.model.enums.MarketDataChannelType;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamHealthState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import com.trading.control.application.domain.model.stream.ConfiguredStream;
import com.trading.control.application.domain.model.stream.chanel.StreamChannelConfig;

import java.util.List;

public final class StaticStreams {
    private StaticStreams() {
    }

    public static List<ConfiguredStream> getConfiguredStreams() {
        ConfiguredStream stream = ConfiguredStream.builder()
                .id("stream-1001")
                .instrumentId("BINANCE:SPOT:BTC:USDT")
                .instrumentKey("BINANCE_SPOT_BTC_USDT")
                .pair("BTC/USDT")
                .symbol("BTCUSDT")
                .exchange("BINANCE")
                .market("SPOT")
                .baseAsset("BTC")
                .quoteAsset("USDT")
                .channel(StreamChannelConfig.builder()
                        .type(MarketDataChannelType.TRADE)
                        .build())
                .channel(StreamChannelConfig.builder()
                        .type(MarketDataChannelType.DEPTH_DIFF)
                        .param("updateSpeed", "100ms")
                        .build())
                .desired(StreamDesiredState.ENABLED)
                .runtime(StreamRuntimeState.RUNNING)
                .health(StreamHealthState.HEALTHY)
                .autoRestart(false)
                .build();

        ConfiguredStream stream1 = ConfiguredStream.builder()
                .id("stream-1002")
                .instrumentId("BINANCE:SPOT:ETH:USDT")
                .instrumentKey("BINANCE_SPOT_ETH_USDT")
                .pair("ETH/USDT")
                .symbol("ETHUSDT")
                .exchange("BINANCE")
                .market("SPOT")
                .baseAsset("ETH")
                .quoteAsset("USDT")
                .channel(StreamChannelConfig.builder()
                        .type(MarketDataChannelType.TRADE)
                        .build())
                .channel(StreamChannelConfig.builder()
                        .type(MarketDataChannelType.DEPTH_DIFF)
                        .param("updateSpeed", "100ms")
                        .build())
                .desired(StreamDesiredState.ENABLED)
                .runtime(StreamRuntimeState.RUNNING)
                .health(StreamHealthState.HEALTHY)
                .autoRestart(false)
                .build();

        ConfiguredStream stream2 = ConfiguredStream.builder()
                .id("stream-1003")
                .instrumentId("BINANCE:SPOT:SOL:USDT")
                .instrumentKey("BINANCE_SPOT_SOL_USDT")
                .pair("SOL/USDT")
                .symbol("SOLUSDT")
                .exchange("BINANCE")
                .market("SPOT")
                .baseAsset("SOL")
                .quoteAsset("USDT")
                .channel(StreamChannelConfig.builder()
                        .type(MarketDataChannelType.TRADE)
                        .build())
                .desired(StreamDesiredState.DISABLED)
                .runtime(StreamRuntimeState.STOPPED)
                .health(StreamHealthState.UNKNOWN)
                .autoRestart(false)
                .build();
        return List.of(stream, stream2, stream1);
    }
}
