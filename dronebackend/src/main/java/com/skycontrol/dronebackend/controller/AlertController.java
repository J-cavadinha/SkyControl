package com.skycontrol.dronebackend.controller;

import com.skycontrol.dronebackend.database.JsonDatabaseService;
import com.skycontrol.dronebackend.model.Alert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*") // Permite acesso do frontend
public class AlertController {

    private final JsonDatabaseService dbService;

    public AlertController(JsonDatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * Endpoint para buscar TODOS os alertas salvos no alerts.json
     */
    @GetMapping
    public List<Alert> getAllAlerts() {
        // Usa o método que já existe no JsonDatabaseService
        return dbService.loadAlerts(); 
    }
}