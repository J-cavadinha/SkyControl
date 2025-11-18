package com.skycontrol.dronebackend.controller;

import com.skycontrol.dronebackend.database.JsonDatabaseService;
import com.skycontrol.dronebackend.model.DroneTelemetry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@CrossOrigin(origins = "*") // Essencial para o frontend acessar
public class TelemetryController {

    private final JsonDatabaseService dbService;

    // Injeta o serviço de banco de dados
    public TelemetryController(JsonDatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * Endpoint para buscar o último estado de telemetria salvo
     * de TODOS os drones.
     */
    @GetMapping("/latest")
    public List<DroneTelemetry> getLatestTelemetry() {
        // Usa o método que ativamos no JsonDatabaseService
        return dbService.loadTelemetry(); //
    }
}
