package com.skycontrol.dronesimulator.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycontrol.dronesimulator.config.RabbitMQConfig;
import com.skycontrol.dronesimulator.service.SimulationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommandListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimulationService simulationService; //

    /**
     * Ouve a fila de Comandos (definida em RabbitMQConfig).
     *
     */
    @RabbitListener(queues = RabbitMQConfig.COMMAND_QUEUE)
    public void handleCommandMessage(String commandJson) {
        try {
            // 1. Desserializa o JSON
            Map<String, Object> commandPayload = objectMapper.readValue(commandJson, 
                new TypeReference<Map<String, Object>>() {});

            String command = (String) commandPayload.get("command");
            
            // Pega o ID (que o DroneCommandService adicionou)
            Long droneId = ((Number) commandPayload.get("droneId")).longValue();

            if (command == null || droneId == null) {
                System.err.println("[CommandListener] Comando inválido recebido: " + commandJson);
                return;
            }
            

            // 2. Encaminha o comando para o SimulationService
            if (command.equals("GO_TO_POSITION")) {
                
                Double lat = ((Number) commandPayload.get("lat")).doubleValue();
                Double lng = ((Number) commandPayload.get("lng")).doubleValue();
                
                if (lat != null && lng != null) {
                    // Chama o método no SimulationService (que vamos criar)
                    simulationService.setDroneTarget(droneId, lat, lng);
                }
            }
            else if (command.equals("CONTINUE")) {
                simulationService.resumeSimulation(droneId);
                System.out.println("[CommandListener] Drone " + droneId + " RESUMIDO por comando externo.");
            }

        } catch (Exception e) {
            System.err.println("[CommandListener] Erro ao processar comando: " + e.getMessage());
        }
    }
}