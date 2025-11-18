package com.skycontrol.dronebackend.service;

import com.skycontrol.dronebackend.model.Drone;
import com.skycontrol.dronebackend.simulator.SimulatedDrone;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SimulationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private ExecutorService executor;
    private Map<Long, SimulatedDrone> activeSimulations = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("--- [SIMULADOR DE DRONES] INICIADO NA PORTA 8081 ---");
        this.executor = Executors.newCachedThreadPool();

        // INICIALIZAÇÃO TEMPORÁRIA: Cria 5 drones "fakes" para teste
        // (Depois faremos isso via RabbitMQ vindo do Backend)
        for (long i = 1; i <= 5; i++) {
            Drone d = new Drone(i, "Drone " + i, "Simulado", true);
            startSimulation(d);
        }
    }

    public void startSimulation(Drone drone) {
        if (activeSimulations.containsKey(drone.getId())) return;

        SimulatedDrone simTask = new SimulatedDrone(drone, rabbitTemplate);
        activeSimulations.put(drone.getId(), simTask);
        executor.submit(simTask);
        System.out.println("[Simulador] Drone " + drone.getId() + " decolou.");
    }

    public void stopSimulation(Long droneId) {
        SimulatedDrone simTask = activeSimulations.get(droneId);
        if (simTask != null) {
            simTask.stop();
            activeSimulations.remove(droneId);
            System.out.println("[Simulador] Drone " + droneId + " pousou/parou.");
        }
    }
    
    // Métodos de controle (usados pelos Listeners depois)
    public void resumeSimulation(Long droneId) {
        SimulatedDrone sim = activeSimulations.get(droneId);
        if (sim != null) sim.resume();
    }

    public void setDroneTarget(Long droneId, double lat, double lng) {
        SimulatedDrone sim = activeSimulations.get(droneId);
        if (sim != null) sim.setTargetPosition(lat, lng);
    }

    @PreDestroy
    public void cleanup() {
        executor.shutdownNow();
    }
}