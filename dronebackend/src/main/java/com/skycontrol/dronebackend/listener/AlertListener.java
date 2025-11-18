package com.skycontrol.dronebackend.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycontrol.dronebackend.config.RabbitMQConfig;
import com.skycontrol.dronebackend.model.Alert;
import com.skycontrol.dronebackend.service.AlertService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlertListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlertService alertService; // O serviço que já criamos

    /**
     * Ouve a fila de Alertas (definida em RabbitMQConfig).
     * Esta fila recebe alertas de emergência dos drones.
     */
    // Dentro de AlertListener.java
    @RabbitListener(queues = RabbitMQConfig.ALERT_QUEUE) //
    public void handleAlertMessage(String alertJson) {
        try {
            Alert alert = objectMapper.readValue(alertJson, Alert.class); //

            System.out.println("[AlertListener] Alerta de Emergência RECEBIDO do Drone ID: " + alert.getDroneId());
            alertService.processExternalAlert(alert); 

        } catch (Exception e) {
            System.err.println("[AlertListener] Erro ao processar alerta recebido: " + e.getMessage());
        }
    }
}