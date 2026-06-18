package com.trading.control.rest;

import com.trading.control.application.port.input.StreamService;
import com.trading.control.rest.mapper.StreamWebMapper;
import com.trading.control.restapi.generated.api.StreamsApi;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import com.trading.control.restapi.generated.model.StreamResponseWebDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StreamsController implements StreamsApi {

    private final StreamService streamService;
    private final StreamWebMapper streamWebMapper;

    public StreamsController(StreamService streamService, StreamWebMapper streamWebMapper) {
        this.streamService = streamService;
        this.streamWebMapper = streamWebMapper;
    }

    @Override
    public List<StreamResponseWebDto> getStreams() {
        return streamService.getConfiguredStreams().stream()
                .map(streamWebMapper::toStreamResponse)
                .toList();
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public StreamResponseWebDto createStream(CreateStreamRequestWebDto createStreamRequestWebDto) {
        var created = streamService.createStream(streamWebMapper.toStreamDefinition(createStreamRequestWebDto));
        return streamWebMapper.toStreamResponse(created);
    }

    @Override
    public StreamResponseWebDto startStream(String streamId) {
        return streamWebMapper.toStreamResponse(streamService.startStream(streamId));
    }

    @Override
    public StreamResponseWebDto stopStream(String streamId) {
        return streamWebMapper.toStreamResponse(streamService.stopStream(streamId));
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStream(String streamId) {
        streamService.deleteStream(streamId);
    }
}
