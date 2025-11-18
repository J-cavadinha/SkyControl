package com.skycontrol.dronebackend.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycontrol.dronebackend.config.RabbitMQConfig;
import com.skycontrol.dronebackend.database.JsonDatabaseService;
import com.skycontrol.dronebackend.events.InternalEventBus;
import com.skycontrol.dronebackend.events.InternalEventPublisher;
import com.skycontrol.dronebackend.model.DroneTelemetry;
import com.skycontrol.dronebackend.service.AlertService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelemetryListener {

    private final InternalEventBus eventBus;
    private final ObjectMapper objectMapper;
    private final JsonDatabaseService dbService;
    private final AlertService alertService; 

    // Mapa para rastrear o último estado da bateria e evitar spam de alertas
    private final Map<Integer, Integer> lastBatteryState = new ConcurrentHashMap<>();

    public TelemetryListener(InternalEventBus eventBus, 
                             ObjectMapper objectMapper,
                             JsonDatabaseService dbService,
                             AlertService alertService) { 
        this.eventBus = eventBus;
        this.objectMapper = objectMapper;
        this.dbService = dbService;
        this.alertService = alertService; 
    }

    @RabbitListener(queues = RabbitMQConfig.TELEMETRY_QUEUE)
    public void handleTelemetryMessage(String telemetryJson) {
        try {
            // 1. Desserializar
            DroneTelemetry telemetry = objectMapper.readValue(telemetryJson, DroneTelemetry.class);

            // 2. Salvar o estado mais recente no JSON
            dbService.saveTelemetry(telemetry); //

            // 3. Publicar no barramento interno (para o frontend)
            var event = new InternalEventPublisher.Event("TELEMETRY_UPDATED", telemetry);
            eventBus.publish(event); //

            // 4. --- LÓGICA DE ALERTA ---
            checkBatteryAlert(telemetry);

        } catch (Exception e) {
            System.err.println("[TelemetryListener] Erro ao processar telemetria: " + e.getMessage());
        }
    }

    /**
     * Verifica a telemetria e dispara um alerta de bateria baixa se necessário.
     * Evita spam de alertas verificando o último estado conhecido.
     */
    // Dentro de TelemetryListener.java
    private void checkBatteryAlert(DroneTelemetry telemetry) {
        final int LOW_BATTERY_THRESHOLD = 20; 
        int currentBattery = telemetry.getBattery();
        int droneId = telemetry.getDroneId();

        int lastNotifiedBattery = lastBatteryState.getOrDefault(droneId, 101); 

        if (currentBattery <= LOW_BATTERY_THRESHOLD && lastNotifiedBattery > LOW_BATTERY_THRESHOLD) {
            
            // --- MUDANÇA AQUI ---
            // Deve chamar o método para alertas INTERNOS
            alertService.createInternalAlert( 
                (long) droneId, 
                "LOW_BATTERY", 
                "Bateria do Drone " + droneId + " está em " + currentBattery + "%!"
            );
            // --- FIM DA MUDANÇA ---
            
            lastBatteryState.put(droneId, currentBattery);
        
        } else if (currentBattery > LOW_BATTERY_THRESHOLD) {
            lastBatteryState.put(droneId, 101);
        }
    }
}