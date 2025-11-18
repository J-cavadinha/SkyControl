package com.skycontrol.dronebackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycontrol.dronebackend.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DroneCommandService {

    @Autowired
    private RabbitTemplate rabbitTemplate; // Para enviar comandos EXTERNOS (para o drone)

    @Autowired
    private SimulationService simulationService; // Para comandos INTERNOS (para o simulador)

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Recebe um comando genérico (do DroneCommandController) e decide para onde enviá-lo.
     */
    public void processCommand(Long droneId, Map<String, Object> payload) {
        
        String command = (String) payload.get("command");
        if (command == null) return;

        // ---------------------------------------------------------------------
        // 1. COMANDOS INTERNOS (Que controlam o SIMULADOR)
        // ---------------------------------------------------------------------
        if (command.equals("CONTINUE")) {
            // Este comando é para o SIMULADOR, então chamamos o serviço diretamente.
            simulationService.resumeSimulation(droneId); //
        
        } 
        
        // ---------------------------------------------------------------------
        // 2. COMANDOS EXTERNOS (Que controlam o DRONE)
        // ---------------------------------------------------------------------
        else if (command.equals("GO_TO_POSITION") || command.equals("SET_FORMATION")) {
            // Estes comandos são para o DRONE, então publicamos no RabbitMQ.
            publishCommandToRabbitMQ(droneId, payload);
        
        } 
        
        else {
            System.err.println("[DroneCommandService] Comando desconhecido: " + command);
        }
    }

    /**
     * Publica um payload de comando no exchange de comandos do RabbitMQ.
     */
    private void publishCommandToRabbitMQ(Long droneId, Map<String, Object> payload) {
        try {
            String routingKey = "drone." + droneId + ".command"; //
            
            // --- IMPORTANTE ---
            // Adiciona o droneId ao payload antes de enviar
            payload.put("droneId", droneId);
            // --- FIM DA ADIÇÃO ---
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            rabbitTemplate.convertAndSend(RabbitMQConfig.COMMAND_EXCHANGE, routingKey, jsonPayload); //

            System.out.println("[DroneCommandService] Comando EXTERNO publicado para RabbitMQ: " + jsonPayload);

        } catch (Exception e) {
            System.err.println("[DroneCommandService] Falha ao publicar comando no RabbitMQ: " + e.getMessage());
        }
    }
    
}