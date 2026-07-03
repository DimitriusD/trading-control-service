package com.trading.control.marketdata.mapper;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.chanel.ChannelParam;
import com.trading.control.application.domain.model.chanel.ChannelParamValue;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamHealthState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import com.trading.control.application.domain.model.instrument.StreamInstrument;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.mds.client.model.ChannelDefinitionDto;
import com.trading.mds.client.model.ChannelTypeDto;
import com.trading.mds.client.model.CreateStreamRequestDto;
import com.trading.mds.client.model.HealthStateDto;
import com.trading.mds.client.model.InstrumentDto;
import com.trading.mds.client.model.RuntimeStateDto;
import com.trading.mds.client.model.StreamResponseDto;
import com.trading.mds.client.model.StreamStateDto;
import com.trading.mds.client.model.StreamStatusDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarketDataStreamMapperTest {

    private static final String INSTRUMENT_ID = "BINANCE|SPOT|BTC|USDT";

    private final MarketDataStreamMapper mapper = new MarketDataStreamMapperImpl();

    @Test
    void mapsResolvedInstrumentToMdsInstrumentDto() {
        StreamInstrument instrument = streamInstrument();

        InstrumentDto dto = mapper.toInstrumentDto(instrument);

        assertEquals(INSTRUMENT_ID, dto.getInstrumentId());
        assertEquals("BINANCE", dto.getExchangeCode());
        assertEquals("SPOT", dto.getMarketCode());
        assertEquals("BTC", dto.getBaseAssetCode());
        assertEquals("USDT", dto.getQuoteAssetCode());
        assertEquals("BTCUSDT", dto.getExchangeSymbol());
    }

    @Test
    void buildsCreateRequestWithFullInstrumentAndChannelParams() {
        StreamDefinition definition = StreamDefinition.builder()
                .instrument(streamInstrument())
                .desiredState(StreamDesiredState.ENABLED)
                .channel(Channel.builder().code("TRADE").enabled(true).build())
                .channel(Channel.builder().code("DEPTH_DIFF").enabled(true)
                        .param(ChannelParam.builder().key("updateSpeed")
                                .value(ChannelParamValue.builder().value("100ms").build()).build())
                        .build())
                .build();

        CreateStreamRequestDto request = mapper.toCreateRequest(definition);

        assertEquals(INSTRUMENT_ID, request.getInstrument().getInstrumentId());
        assertEquals("BTCUSDT", request.getInstrument().getExchangeSymbol());
        assertEquals(StreamStateDto.ENABLED, request.getDesiredState());
        assertEquals(2, request.getChannels().size());

        ChannelDefinitionDto depth = request.getChannels().stream()
                .filter(c -> c.getType() == ChannelTypeDto.DEPTH_DIFF)
                .findFirst().orElseThrow();
        assertEquals(Boolean.TRUE, depth.getEnabled());
        assertEquals("100ms", depth.getParams().get("updateSpeed"));
    }

    @Test
    void mapsMdsResponseWithNewInstrumentToStreamDefinition() {
        InstrumentDto instrument = new InstrumentDto();
        instrument.setInstrumentId(INSTRUMENT_ID);
        instrument.setExchangeCode("BINANCE");
        instrument.setMarketCode("SPOT");
        instrument.setBaseAssetCode("BTC");
        instrument.setQuoteAssetCode("USDT");
        instrument.setExchangeSymbol("BTCUSDT");

        StreamStatusDto status = new StreamStatusDto();
        status.setRuntime(RuntimeStateDto.RUNNING);
        status.setHealth(HealthStateDto.HEALTHY);

        ChannelDefinitionDto channel = new ChannelDefinitionDto();
        channel.setType(ChannelTypeDto.DEPTH_DIFF);
        channel.setEnabled(true);
        channel.setParams(Map.of("updateSpeed", "100ms"));

        // streamId is a read-only (server-owned) property, so it is set via the constructor.
        StreamResponseDto response = new StreamResponseDto("binance-spot-btc-usdt");
        response.setInstrument(instrument);
        response.setDesiredState(StreamStateDto.ENABLED);
        response.setStatus(status);
        response.setChannels(List.of(channel));

        StreamDefinition definition = mapper.toStreamDefinition(response);

        assertEquals("binance-spot-btc-usdt", definition.getStreamId());
        assertEquals(INSTRUMENT_ID, definition.getInstrument().getInstrumentId());
        assertEquals("BTCUSDT", definition.getInstrument().getExchangeSymbol());
        assertEquals(StreamDesiredState.ENABLED, definition.getDesiredState());
        assertEquals(StreamRuntimeState.RUNNING, definition.getRuntime());
        assertEquals(StreamHealthState.HEALTHY, definition.getHealth());
        assertEquals(1, definition.getChannels().size());
        assertEquals("DEPTH_DIFF", definition.getChannels().get(0).getCode());
        assertEquals("100ms", definition.getChannels().get(0).getParams().get(0).getValues().get(0).getValue());
    }

    private static StreamInstrument streamInstrument() {
        return StreamInstrument.builder()
                .instrumentId(INSTRUMENT_ID)
                .exchangeCode("BINANCE")
                .marketCode("SPOT")
                .baseAssetCode("BTC")
                .quoteAssetCode("USDT")
                .exchangeSymbol("BTCUSDT")
                .build();
    }
}
