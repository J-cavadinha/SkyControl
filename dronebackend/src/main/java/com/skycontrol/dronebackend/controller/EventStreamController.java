package com.skycontrol.dronebackend.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import com.skycontrol.dronebackend.events.InternalEventBus;

@RestController
@CrossOrigin(origins = "*")
public class EventStreamController {

    private final InternalEventBus eventBus;

    public EventStreamController(InternalEventBus eventBus) {
        this.eventBus = eventBus;
    }

    // Fluxo contínuo de eventos no formato text/event-stream
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> streamEvents() {
        return eventBus.getEventFlux();
    }
}
