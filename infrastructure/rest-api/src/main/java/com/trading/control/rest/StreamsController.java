package com.trading.control.rest;

import com.trading.control.application.port.input.StreamService;
import com.trading.control.rest.mapper.StreamWebMapper;
import com.trading.control.restapi.generated.api.StreamsApi;
import com.trading.control.restapi.generated.model.ConfiguredStreamWebDto;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StreamsController implements StreamsApi {

    private final StreamService streamService;

    public StreamsController(StreamService streamService) {
        this.streamService = streamService;
    }

    @Override
    public List<ConfiguredStreamWebDto> getConfiguredStreams() {
        return streamService.getConfiguredStreams().stream().map(StreamWebMapper::toConfiguredStream).toList();
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ConfiguredStreamWebDto createStream(CreateStreamRequestWebDto createStreamRequestWebDto) {
        var created = streamService.createStream(StreamWebMapper.toCreateStreamCommand(createStreamRequestWebDto));
        return StreamWebMapper.toConfiguredStream(created);
    }

    @Override
    public ConfiguredStreamWebDto startStream(String streamId) {
        return StreamWebMapper.toConfiguredStream(streamService.startStream(streamId));
    }

    @Override
    public ConfiguredStreamWebDto stopStream(String streamId) {
        return StreamWebMapper.toConfiguredStream(streamService.stopStream(streamId));
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStream(String streamId) {
        streamService.deleteStream(streamId);
    }
}
