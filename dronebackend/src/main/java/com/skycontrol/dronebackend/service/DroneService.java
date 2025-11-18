package com.skycontrol.dronebackend.service;

import com.skycontrol.dronebackend.model.Drone;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.*;
import com.skycontrol.dronebackend.database.JsonDatabaseService;
import com.skycontrol.dronebackend.events.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DroneService {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private JsonDatabaseService db; //

    private List<Drone> drones = new ArrayList<>();

    @PostConstruct
    public void init() {
        drones = db.load();
        if (drones == null) drones = new ArrayList<>();
    }

    public List<Drone> getAll() {
        return new ArrayList<>(drones);
    }

    public Drone create(Drone drone) {
        drone.setId(generateId());
        drones.add(drone);
        
        db.save(drones);
        eventPublisher.publish("DRONE_CREATED", drone);

        return drone;
    }

    public Drone getById(Long id) {
        return drones.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Drone update(Long id, Drone newDrone) {
        Drone drone = getById(id);
        if (drone == null) return null;
        if (newDrone == null) return drone;

        if (newDrone.getName() != null) drone.setName(newDrone.getName());
        if (newDrone.getModel() != null) drone.setModel(newDrone.getModel());
        if (newDrone.getActive() != null) drone.setActive(newDrone.getActive());

        db.save(drones);
        eventPublisher.publish("DRONE_UPDATED", drone);

        return drone;
    }

    public boolean delete(Long id) {
        Drone drone = getById(id);
        if (drone == null) return false;

        // 1. Remove da lista em memória
        drones.removeIf(d -> d.getId().equals(id));
        
        // 2. Salva a lista atualizada de drones
        db.save(drones);
        
        // 3. --- Arquiva a telemetria antiga ---
        db.archiveTelemetry(id); //

        // 4. Notifica o sistema
        eventPublisher.publish("DRONE_DELETED", drone);

        return true;
    }

    private Long generateId() {
        return drones.stream()
                .mapToLong(Drone::getId)
                .max()
                .orElse(0L) + 1;
    }
}