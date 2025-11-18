package com.skycontrol.dronebackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycontrol.dronebackend.model.Drone;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {
    // Serviço responsável por enviar mensagens para o RabbitMQ

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    // 1) Envio genérico de eventos
    public void sendEvent(Object payload, String eventType) {
        try {
            EventWrapper wrapper = new EventWrapper(eventType, payload);
            String json = objectMapper.writeValueAsString(wrapper);

            rabbitTemplate.convertAndSend(exchange, routingKey, json);
            System.out.println("[RabbitMQProducer] evento enviado: " + json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    // 2) Método legado (continua funcionando)
    public void sendDroneEvent(Drone drone, String eventType) {
        sendEvent(drone, eventType); // usa o novo padronizado
    }

    
    // Envio simples (string) 
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        System.out.println("[RabbitMQProducer] mensagem enviada: " + message);
    }

    // Classe wrapper para padronizar
    public static class EventWrapper {
        private String eventType;
        private Object payload;

        public EventWrapper() {}

        public EventWrapper(String eventType, Object payload) {
            this.eventType = eventType;
            this.payload = payload;
        }

        public String getEventType() { return eventType; }
        public Object getPayload() { return payload; }

        public void setEventType(String eventType) { this.eventType = eventType; }
        public void setPayload(Object payload) { this.payload = payload; }
    }
}
