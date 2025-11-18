package com.skycontrol.dronebackend.controller;

import com.skycontrol.dronebackend.service.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/formations")
@CrossOrigin(origins = "*")
public class FormationController {

    @Autowired
    private FormationService formationService; //

    /**
     * Endpoint para definir a formação.
     * Recebe: { "type": "LINE", "leaderId": 5 } (leaderId é opcional)
     */
    @PostMapping
    public ResponseEntity<Void> setFormation(@RequestBody Map<String, Object> payload) {
        String type = (String) payload.get("type");
        
        // Tenta pegar o ID do líder (pode ser nulo)
        Long leaderId = null;
        if (payload.containsKey("leaderId") && payload.get("leaderId") != null) {
            leaderId = ((Number) payload.get("leaderId")).longValue();
        }
        
        if (type != null) {
            // Chama o serviço passando o tipo e o líder
            formationService.applyFormation(type, leaderId);
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.badRequest().build();
    }
}