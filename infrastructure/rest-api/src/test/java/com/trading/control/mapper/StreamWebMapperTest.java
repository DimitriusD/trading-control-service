package com.trading.control.mapper;

import com.trading.control.application.domain.model.chanel.Channel;
import com.trading.control.application.domain.model.enums.StreamDesiredState;
import com.trading.control.application.domain.model.enums.StreamHealthState;
import com.trading.control.application.domain.model.enums.StreamRuntimeState;
import com.trading.control.application.domain.model.instrument.StreamInstrument;
import com.trading.control.application.domain.model.stream.CreateStreamCommand;
import com.trading.control.application.domain.model.stream.StreamDefinition;
import com.trading.control.restapi.generated.model.ChannelParamSelectionWebDto;
import com.trading.control.restapi.generated.model.ChannelSelectionWebDto;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import com.trading.control.restapi.generated.model.HealthStateWebDto;
import com.trading.control.restapi.generated.model.RuntimeStateWebDto;
import com.trading.control.restapi.generated.model.StreamResponseWebDto;
import com.trading.control.restapi.generated.model.StreamStateWebDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamWebMapperTest {

    private static final String INSTRUMENT_ID = "BINANCE|SPOT|BTC|USDT";

    private final StreamWebMapper mapper = new StreamWebMapperImpl();

    @Test
    void createRequestWithOnlyInstrumentIdAndChannelsMapsToCommand() {
        ChannelParamSelectionWebDto param = new ChannelParamSelectionWebDto();
        param.setKey("updateSpeed");
        param.setValue("100ms");

        ChannelSelectionWebDto channel = new ChannelSelectionWebDto();
        channel.setCode("DEPTH_DIFF");
        channel.setEnabled(true);
        channel.setParams(List.of(param));

        CreateStreamRequestWebDto request = new CreateStreamRequestWebDto();
        request.setInstrumentId(INSTRUMENT_ID);
        request.setDesiredState(StreamStateWebDto.ENABLED);
        request.setChannels(List.of(channel));

        CreateStreamCommand command = mapper.toCreateCommand(request);

        assertEquals(INSTRUMENT_ID, command.getInstrumentId());
        assertEquals(StreamDesiredState.ENABLED, command.getDesiredState());
        assertEquals(1, command.getChannels().size());
        assertEquals("DEPTH_DIFF", command.getChannels().get(0).getCode());
        assertEquals("100ms",
                command.getChannels().get(0).getParams().get(0).getValues().get(0).getValue());
    }

    @Test
    void streamDefinitionMapsToResponseWithInstrumentAndStatus() {
        StreamDefinition definition = StreamDefinition.builder()
                .streamId("binance-spot-btc-usdt")
                .instrument(StreamInstrument.builder()
                        .instrumentId(INSTRUMENT_ID)
                        .exchangeCode("BINANCE").marketCode("SPOT")
                        .baseAssetCode("BTC").quoteAssetCode("USDT").exchangeSymbol("BTCUSDT")
                        .build())
                .desiredState(StreamDesiredState.ENABLED)
                .runtime(StreamRuntimeState.UNKNOWN)
                .health(StreamHealthState.UNKNOWN)
                .channel(Channel.builder().code("TRADE").enabled(true).build())
                .build();

        StreamResponseWebDto response = mapper.toStreamResponse(definition);

        assertEquals("binance-spot-btc-usdt", response.getStreamId());
        assertEquals(INSTRUMENT_ID, response.getInstrument().getInstrumentId());
        assertEquals("BTCUSDT", response.getInstrument().getExchangeSymbol());
        assertEquals(StreamStateWebDto.ENABLED, response.getDesiredState());
        assertEquals(RuntimeStateWebDto.UNKNOWN, response.getStatus().getRuntime());
        assertEquals(HealthStateWebDto.UNKNOWN, response.getStatus().getHealth());
        assertEquals(1, response.getChannels().size());
        assertEquals("TRADE", response.getChannels().get(0).getCode());
    }
}
