package com.skycontrol.dronebackend.events;

public interface EventPublisher { 
    // método genérico para publicar eventos
    void publish(String eventType, Object data);
}

// Implementação específica usando RabbitMQ está em RabbitEventPublisher.java
// O droneService faz uso dessa interface para publicar eventos sem depender diretamente do RabbitMQ.