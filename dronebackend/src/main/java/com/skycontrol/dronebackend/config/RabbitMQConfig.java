package com.skycontrol.dronebackend.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {   

    //-----------------------
    // 1) TELEMETRIA
    //-----------------------
    public static final String TELEMETRY_EXCHANGE = "telemetry.exchange";
    public static final String TELEMETRY_QUEUE = "telemetry.queue";
    public static final String TELEMETRY_ROUTING_KEY = "drone.*.telemetry";

    @Bean
    public TopicExchange telemetryExchange() {
        return new TopicExchange(TELEMETRY_EXCHANGE);
    }

    @Bean
    public Queue telemetryQueue() {
        return new Queue(TELEMETRY_QUEUE, true);
    }

    @Bean
    public Binding telemetryBinding() {
        return BindingBuilder
                .bind(telemetryQueue())
                .to(telemetryExchange())
                .with(TELEMETRY_ROUTING_KEY);
    }


    //-----------------------
    // 2) ALERTAS
    //-----------------------
    public static final String ALERT_EXCHANGE = "alert.exchange";
    public static final String ALERT_QUEUE = "alert.queue";
    public static final String ALERT_ROUTING_KEY = "drone.*.alert";

    @Bean
    public TopicExchange alertExchange() {
        return new TopicExchange(ALERT_EXCHANGE);
    }

    @Bean
    public Queue alertQueue() {
        return new Queue(ALERT_QUEUE, true);
    }

    @Bean
    public Binding alertBinding() {
        return BindingBuilder
                .bind(alertQueue())
                .to(alertExchange())
                .with(ALERT_ROUTING_KEY);
    }


    //-----------------------
    // 3) COMANDOS DO FRONT → DRONE
    //-----------------------
    public static final String COMMAND_EXCHANGE = "command.exchange";
    public static final String COMMAND_QUEUE = "command.queue";
    public static final String COMMAND_ROUTING_KEY = "drone.*.command";

    @Bean
    public TopicExchange commandExchange() {
        return new TopicExchange(COMMAND_EXCHANGE);
    }

    @Bean
    public Queue commandQueue() {
        return new Queue(COMMAND_QUEUE, true);
    }

    @Bean
    public Binding commandBinding() {
        return BindingBuilder
                .bind(commandQueue())
                .to(commandExchange())
                .with(COMMAND_ROUTING_KEY);
    }


    //-----------------------
    // 4) EVENTOS DE SISTEMA (opcional)
    //-----------------------
    public static final String EVENT_EXCHANGE = "event.exchange";
    public static final String EVENT_QUEUE = "event.queue";
    public static final String EVENT_ROUTING_KEY = "drone.*.event";

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EVENT_EXCHANGE);
    }

    @Bean
    public Queue eventQueue() {
        return new Queue(EVENT_QUEUE, true);
    }

    @Bean
    public Binding eventBinding() {
        return BindingBuilder
                .bind(eventQueue())
                .to(eventExchange())
                .with(EVENT_ROUTING_KEY);
    }
}
