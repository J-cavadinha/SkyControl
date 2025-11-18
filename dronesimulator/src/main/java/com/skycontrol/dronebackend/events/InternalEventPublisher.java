package com.skycontrol.dronebackend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class InternalEventPublisher implements EventPublisher {

    @Autowired
    private InternalEventBus internalEventBus;

    @Override
    public void publish(String eventType, Object data) {

        var event = new Event(eventType, data);
        internalEventBus.publish(event);
    }

    public static class Event {
        public String type;
        public Object data;

        public Event(String type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    
}
