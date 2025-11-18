package com.skycontrol.dronebackend.model;

public class Drone {
    private Long id;
    private String name;
    private String model;
    private boolean active;

    public Drone() {}

    public Drone(Long id, String name, String model, boolean active) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.active = active;
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public boolean isActive() { return active; }
    public Boolean getActive() { return active; } // Getter para Boolean
    public void setActive(boolean active) { this.active = active; }
}

