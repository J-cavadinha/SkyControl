package com.skycontrol.dronebackend.controller;

import com.skycontrol.dronebackend.model.Drone;
import com.skycontrol.dronebackend.service.DroneService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//Não usamos o ResponseEntity aqui para simplificar o código (Aprendizado inicial)
//O ResponseEntity é usado para Retornar códigos HTTP específicos
//Como não vamos usa-lo, a resposta sempre será 200 OK para sucesso e 500 Internal Server Error para falhas

@CrossOrigin(origins = "*") //Permitir requisições de qualquer origem (frontend)
@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @PostMapping
    public Drone create(@RequestBody Drone drone) {
        return droneService.create(drone);
    }

    @GetMapping
    public List<Drone> getAll() {
        return droneService.getAll();
    }

    @GetMapping("/{id}")
    public Drone getById(@PathVariable Long id) {
        return droneService.getById(id);
    }

    @PutMapping("/{id}")
    public Drone update(@PathVariable Long id, @RequestBody Drone drone) {
        return droneService.update(id, drone);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        droneService.delete(id);
    }
}
