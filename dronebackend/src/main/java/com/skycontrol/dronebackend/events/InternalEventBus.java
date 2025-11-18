package com.skycontrol.dronebackend.events;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class InternalEventBus {

    private final Sinks.Many<Object> sink;
    private final Flux<Object> flux;

    public InternalEventBus() {
        this.sink = Sinks.many().multicast().directAllOrNothing();
        this.flux = sink.asFlux();
    }

    public void publish(Object event) {
        sink.tryEmitNext(event);
    }

    public Flux<Object> getEventFlux() {
        return flux;
    }
}
