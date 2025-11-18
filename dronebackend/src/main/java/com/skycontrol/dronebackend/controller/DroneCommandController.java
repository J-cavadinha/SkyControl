package com.skycontrol.dronebackend.controller;

import com.skycontrol.dronebackend.service.DroneCommandService; // <-- 1. MUDAR IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/drones")
@CrossOrigin(origins = "*") //
public class DroneCommandController {

    @Autowired
    private DroneCommandService commandService; // <-- 2. INJETAR O NOVO SERVIÇO

    /**
     * Endpoint para enviar comandos para um drone específico.
     * Ex: /api/drones/5/command
     */
    @PostMapping("/{id}/command")
    public ResponseEntity<Void> receiveCommand(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> payload) { // Mudei para Object para aceitar lat/lng
        
        String command = (String) payload.get("command");
        if (command == null) {
            return ResponseEntity.badRequest().build();
        }

        // --- 3. DELEGAR PARA O SERVIÇO ---
        // O Controller não sabe o que o comando faz,
        // ele apenas o repassa para o CommandService.
        commandService.processCommand(id, payload); //
        
        System.out.println("[CommandController] Comando recebido para Drone ID: " + id + " | Payload: " + payload);
        return ResponseEntity.ok().build(); // Sucesso
    }
}