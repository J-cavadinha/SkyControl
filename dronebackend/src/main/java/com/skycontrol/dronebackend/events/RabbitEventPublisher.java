/*package com.skycontrol.dronebackend.events;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.skycontrol.dronebackend.service.RabbitMQProducer;

@Component
public class RabbitEventPublisher implements EventPublisher {

    @Autowired
    private RabbitMQProducer producer;

    @Override
    public void publish(String eventType, Object payload) {
        producer.sendEvent(payload, eventType);
    }
}
// Implementação específica usando RabbitMQ está em RabbitEventPublisher.java
// O droneService faz uso dessa interf  ace para publicar eventos sem depender diretamente do RabbitMQ.
*/