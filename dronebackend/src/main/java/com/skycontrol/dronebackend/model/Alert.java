package com.skycontrol.dronebackend.model;

public class Alert {
    private Long droneId;
    private String type;       // LOW_BATTERY, EMERGENCY_SIGNAL, etc.
    private String message;
    private long timestamp;

    private double lat;
    private double lng;

   
    //Construtor padrão (vazio) exigido pelo Jackson e RabbitMQ.
    public Alert() {}

    //Construtor completo para o novo formato com lat/lng
    public Alert(Long droneId, String type, String message, long timestamp, double lat, double lng) {
        this.droneId = droneId;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
    }
    
     //Construtor legado (para o LOW_BATTERY)
    public Alert(Long droneId, String type, String message, long timestamp) {
        // Chama o construtor completo com lat/lng zerados
        this(droneId, type, message, timestamp, 0.0, 0.0); 
    }


    // --- Getters e Setters para TODOS os campos ---
    
    public Long getDroneId() { return droneId; }
    public void setDroneId(Long droneId) { this.droneId = droneId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
}