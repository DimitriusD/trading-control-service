package com.trading.control;

import com.trading.control.application.port.input.StreamService;
import com.trading.control.mapper.StreamWebMapper;
import com.trading.control.restapi.generated.api.StreamsApi;
import com.trading.control.restapi.generated.model.CreateStreamRequestWebDto;
import com.trading.control.restapi.generated.model.StreamResponseWebDto;
import com.trading.control.restapi.generated.model.UpdateStreamRequestWebDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class StreamsController implements StreamsApi {

    private final StreamService streamService;
    private final StreamWebMapper streamWebMapper;

    @Override
    public List<StreamResponseWebDto> getStreams() {
        return streamService.getStreams().stream()
                .map(streamWebMapper::toStreamResponse)
                .toList();
    }

    @Override
    public StreamResponseWebDto getStream(String streamId) {
        return streamWebMapper.toStreamResponse(streamService.getStream(streamId));
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public StreamResponseWebDto createStream(CreateStreamRequestWebDto createStreamRequestWebDto) {
        final var createdStream = streamService.createStream(streamWebMapper.toCreateCommand(createStreamRequestWebDto));
        return streamWebMapper.toStreamResponse(createdStream);
    }

    @Override
    public StreamResponseWebDto updateStream(String streamId, UpdateStreamRequestWebDto updateStreamRequestWebDto) {
        final var patch = streamWebMapper.toStreamPatch(updateStreamRequestWebDto);
        return streamWebMapper.toStreamResponse(streamService.updateStream(streamId, patch));
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStream(String streamId) {
        streamService.deleteStream(streamId);
    }
}
