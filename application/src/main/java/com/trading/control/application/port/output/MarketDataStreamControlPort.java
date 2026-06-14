package com.trading.control.application.port.output;

import java.util.List;

public interface MarketDataStreamControlPort {

    List<RemoteStream> listStreams();

    void setStreamEnabled(String streamId, boolean enabled);

    record RemoteStream(String streamId, boolean enabled) {
    }
}
