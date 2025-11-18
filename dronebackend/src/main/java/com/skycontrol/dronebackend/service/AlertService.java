package com.skycontrol.dronebackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycontrol.dronebackend.config.RabbitMQConfig;
import com.skycontrol.dronebackend.database.JsonDatabaseService;
import com.skycontrol.dronebackend.events.InternalEventBus;
import com.skycontrol.dronebackend.events.InternalEventPublisher;
import com.skycontrol.dronebackend.model.Alert;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;


@Service
public class AlertService {

    @Autowired
    private JsonDatabaseService dbService; //
    @Autowired
    private InternalEventBus eventBus; //
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Executado AUTOMATICAMENTE quando o Spring inicia este serviço.
     * Limpa os alertas antigos do JSON.
     */
    @PostConstruct
    public void init() {
        dbService.clearAlerts(); //
    }

    /**
     * Alertas EXTERNOS (do RabbitMQ via AlertListener)
     * APENAS salva e notifica o frontend.
     *
     */
    public void processExternalAlert(Alert alert) {
        
        // 1. Salva no JSON
        dbService.saveAlert(alert); //

        // 2. Publica no INTERNO (para o frontend)
        var event = new InternalEventPublisher.Event("ALERT_CREATED", alert); //
        eventBus.publish(event); //

        System.out.println("!!! ALERTA EXTERNO PROCESSADO !!! Drone ID: " + alert.getDroneId() + " | Tipo: " + alert.getType());
    }

    /**
     * Alertas INTERNOS (Bateria Baixa via TelemetryListener)
     * Salva, notifica o frontend E publica no EXTERNO (RabbitMQ).
     *
     */
    public void createInternalAlert(Long droneId, String type, String message) {
        long timestamp = System.currentTimeMillis();
        Alert alert = new Alert(droneId, type, message, timestamp); //
        
        // 1. Salva no JSON
        dbService.saveAlert(alert); //

        // 2. Publica no INTERNO (para o frontend)
        var event = new InternalEventPublisher.Event("ALERT_CREATED", alert); //
        eventBus.publish(event); //

        // 3. Publica no EXTERNO (para outros sistemas)
        publishAlertToRabbitMQ(alert);

        System.out.println("!!! ALERTA INTERNO GERADO !!! Drone ID: " + alert.getDroneId() + " | Tipo: " + alert.getType());
    }

    /**
     * Helper para enviar alertas para o RabbitMQ
     */
    private void publishAlertToRabbitMQ(Alert alert) {
        try {
            String routingKey = "drone." + alert.getDroneId() + ".alert"; //
            String alertJson = objectMapper.writeValueAsString(alert); 
            rabbitTemplate.convertAndSend(RabbitMQConfig.ALERT_EXCHANGE, routingKey, alertJson); //
        
        } catch (Exception e) {
            System.err.println("[AlertService] Falha ao publicar alerta no RabbitMQ: " + e.getMessage());
        }
    }

    

}