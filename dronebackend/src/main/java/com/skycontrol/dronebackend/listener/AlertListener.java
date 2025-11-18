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
        // --- LINHA DE DEBUG: Mostra se a mensagem chegou ---
        System.out.println("[AlertListener] JSON BRUTO recebido: " + alertJson); 
    
        try {
            Alert alert = objectMapper.readValue(alertJson, Alert.class); 

            System.out.println("[AlertListener] Alerta de Emergência RECEBIDO do Drone ID: " + alert.getDroneId());
            // Se este System.out aparecer, o alerta será processado e publicado para o Frontend.
            alertService.processExternalAlert(alert); 

        } catch (Exception e) {
            // --- LOG MELHORADO: Mostra se a deserialização falhou e qual JSON deu erro ---
            System.err.println("[AlertListener] ERRO CRÍTICO ao processar alerta. JSON: " + alertJson + " | Erro: " + e.getMessage());
        }
    }
}