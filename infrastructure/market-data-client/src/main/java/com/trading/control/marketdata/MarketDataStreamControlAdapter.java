package com.trading.control.marketdata;

import com.trading.control.application.port.output.MarketDataStreamControlPort;
import com.trading.mds.client.api.StreamsApi;
import com.trading.mds.client.model.StreamResponseDto;
import com.trading.mds.client.model.UpdateStreamEnabledRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketDataStreamControlAdapter implements MarketDataStreamControlPort {

    private final StreamsApi streamsApi;

    public MarketDataStreamControlAdapter(StreamsApi streamsApi) {
        this.streamsApi = streamsApi;
    }

    @Override
    public List<RemoteStream> listStreams() {
        List<StreamResponseDto> streams = streamsApi.listStreams();
        return streams.stream()
                .map(dto -> new RemoteStream(dto.getStreamId(), Boolean.TRUE.equals(dto.getEnabled())))
                .toList();
    }

    @Override
    public void setStreamEnabled(String streamId, boolean enabled) {
        streamsApi.updateStreamEnabled(streamId, new UpdateStreamEnabledRequestDto().enabled(enabled));
    }
}
