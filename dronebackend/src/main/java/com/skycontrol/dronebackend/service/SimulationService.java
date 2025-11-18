package com.skycontrol.dronebackend.service;

import com.skycontrol.dronebackend.database.JsonDatabaseService;
import com.skycontrol.dronebackend.events.InternalEventBus;
import com.skycontrol.dronebackend.events.InternalEventPublisher;
import com.skycontrol.dronebackend.model.Drone;
import com.skycontrol.dronebackend.simulator.SimulatedDrone;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gerencia o ciclo de vida das simulações de drones.
 *
 */
@Service
public class SimulationService {

    @Autowired
    private JsonDatabaseService dbService; // Para carregar os drones existentes

    @Autowired
    private RabbitTemplate rabbitTemplate; // Para o SimulatedDrone enviar msgs

    @Autowired
    private InternalEventBus eventBus; // Para ouvir eventos de CRUD

    private ExecutorService executor;
    private Map<Long, SimulatedDrone> activeSimulations = new ConcurrentHashMap<>();
    private Disposable eventBusSubscription;

    @PostConstruct
    public void init() {
        System.out.println("[SimulationService] Iniciando...");
        this.executor = Executors.newCachedThreadPool();

        // 1. Inicia simulações para drones existentes no JSON
        List<Drone> existingDrones = dbService.load();
        System.out.println("[SimulationService] Carregando " + existingDrones.size() + " drones existentes do JSON.");
        for (Drone drone : existingDrones) {
            if (drone.isActive()) { // Só simula se estiver 'ativo' no JSON
                startSimulation(drone);
            }
        }

        // 2. Ouve por eventos futuros
        listenToEvents();
    }

    private void startSimulation(Drone drone) {
        if (activeSimulations.containsKey(drone.getId())) {
            return; // Já está simulando
        }

        SimulatedDrone simTask = new SimulatedDrone(drone, rabbitTemplate); //
        activeSimulations.put(drone.getId(), simTask);
        executor.submit(simTask); // Inicia a thread
    }

    private void stopSimulation(Drone drone) {
        SimulatedDrone simTask = activeSimulations.get(drone.getId());
        if (simTask != null) {
            simTask.stop(); // Diz para a thread parar
            activeSimulations.remove(drone.getId());
            System.out.println("[SimulationService] Simulação PARADA para Drone ID: " + drone.getId());
        }
    }

     /**
     * Envia um comando para "despausar" um drone (Estado: PAUSED -> IDLE).
     * Chamado pelo DroneCommandService quando o comando é "CONTINUE".
     *
     */
    public void resumeSimulation(Long droneId) {
        // Encontra a tarefa de simulação no mapa
        SimulatedDrone simTask = activeSimulations.get(droneId);
        
        if (simTask != null) {
            System.out.println("[SimulationService] Comando 'CONTINUE' recebido. Acordando drone: " + droneId);
            simTask.resume(); // Chama o método 'resume()' que criamos no SimulatedDrone
        } else {
            System.out.println("[SimulationService] Comando 'CONTINUE' recebido, mas drone " + droneId + " não está em simulação ativa.");
        }
    }

    /**
     * Define um novo alvo de GPS para um drone específico (Estado: IDLE -> MOVING).
     * Chamado pelo CommandListener.
     *
     */
    public void setDroneTarget(Long droneId, double lat, double lng) {
        SimulatedDrone simTask = activeSimulations.get(droneId);
        
        if (simTask != null) {
            simTask.setTargetPosition(lat, lng); // Chama o método que criamos no SimulatedDrone
        } else {
            System.err.println("[SimulationService] Comando GO_TO_POSITION recebido, mas drone " + droneId + " não está em simulação.");
        }
    }

    private void listenToEvents() {
        this.eventBusSubscription = eventBus.getEventFlux() //
            .subscribe(event -> {
                if (event instanceof InternalEventPublisher.Event) {
                    InternalEventPublisher.Event appEvent = (InternalEventPublisher.Event) event; //

                    if (appEvent.data instanceof Drone) {
                        Drone drone = (Drone) appEvent.data;

                        switch (appEvent.type) {
                            case "DRONE_CREATED": //
                                System.out.println("[SimulationService] Evento DRONE_CREATED recebido. Iniciando simulação para: " + drone.getId());
                                startSimulation(drone);
                                break;
                            
                            case "DRONE_DELETED": //
                                System.out.println("[SimulationService] Evento DRONE_DELETED recebido. Parando simulação para: " + drone.getId());
                                stopSimulation(drone);
                                break;
                            
                            case "DRONE_UPDATED":
                                // Se o drone for atualizado para 'inativo', para a simulação
                                if (!drone.isActive()) {
                                    System.out.println("[SimulationService] Evento DRONE_UPDATED (inativo) recebido. Parando simulação para: " + drone.getId());
                                    stopSimulation(drone);
                                } else {
                                    // Se for ativado, inicia
                                    startSimulation(drone);
                                }
                                break;
                        }
                    }
                }
            });
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("[SimulationService] Desligando... Parando todas as simulações.");
        if (this.eventBusSubscription != null) {
            this.eventBusSubscription.dispose();
        }
        executor.shutdownNow(); // Força a parada de todas as threads
    }

   
}