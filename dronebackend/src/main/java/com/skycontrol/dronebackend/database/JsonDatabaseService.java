package com.skycontrol.dronebackend.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycontrol.dronebackend.model.Alert;
import com.skycontrol.dronebackend.model.Drone;
import com.skycontrol.dronebackend.model.DroneTelemetry;

import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonDatabaseService {

    private static final String DATA_DIRECTORY;
    static {
        String rootDir = Paths.get("").toAbsolutePath().toString();
        DATA_DIRECTORY = Paths.get(rootDir, "data").toString();
        new File(DATA_DIRECTORY).mkdirs(); 
    }

    private static final String DRONES_FILE = "drones.json";
    private static final String ALERTS_FILE = "alerts.json";
    private static final String TELEMETRY_FILE = "telemetry.json";
    private static final String ARCHIVED_FILE = "archived_telemetry.json"; // <-- NOVO ARQUIVO

    private final ObjectMapper mapper = new ObjectMapper();

    private String getFilePath(String fileName) {
        return Paths.get(DATA_DIRECTORY, fileName).toString();
    }

    // --- DRONES ---
    public synchronized List<Drone> load() {
        try {
            File file = new File(getFilePath(DRONES_FILE));
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<Drone>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar banco JSON", e);
        }
    }

    public synchronized void save(List<Drone> drones) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getFilePath(DRONES_FILE)), drones);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar banco JSON", e);
        }
    }

    // --- ALERTAS ---
    public synchronized void saveAlert(Alert alert) {
        List<Alert> alerts = loadAlerts();
        alerts.add(alert);
        writeFile(getFilePath(ALERTS_FILE), alerts);
    }

    public synchronized List<Alert> loadAlerts() {
        return loadListFromFile(getFilePath(ALERTS_FILE), new TypeReference<List<Alert>>() {});
    }

    public synchronized void clearAlerts() {
        writeFile(getFilePath(ALERTS_FILE), new ArrayList<Alert>());
        System.out.println("[JsonDatabaseService] Arquivo de alertas zerado.");
    }

    // --- TELEMETRIA ---
    public synchronized List<DroneTelemetry> loadTelemetry() {
        return loadListFromFile(getFilePath(TELEMETRY_FILE), new TypeReference<List<DroneTelemetry>>() {});
    }
    
    public synchronized void saveTelemetry(DroneTelemetry telemetry) {
        List<DroneTelemetry> list = loadTelemetry();
        DroneTelemetry existing = list.stream()
                .filter(t -> t.getDroneId() == telemetry.getDroneId())
                .findFirst().orElse(null);

        if (existing == null) {
            list.add(telemetry);
        } else {
            existing.setLat(telemetry.getLat());
            existing.setLng(telemetry.getLng());
            existing.setAltitude(telemetry.getAltitude());
            existing.setSpeed(telemetry.getSpeed());
            existing.setBattery(telemetry.getBattery());
        }
        writeFile(getFilePath(TELEMETRY_FILE), list);
    }

    // --- NOVO: ARQUIVAR TELEMETRIA ---
    public synchronized void archiveTelemetry(Long droneId) {
        // 1. Carrega a telemetria ativa
        List<DroneTelemetry> activeList = loadTelemetry();
        
        // 2. Encontra o drone que está sendo deletado
        DroneTelemetry toArchive = activeList.stream()
                .filter(t -> t.getDroneId() == droneId.intValue())
                .findFirst()
                .orElse(null);

        if (toArchive != null) {
            // 3. Remove da lista ativa e salva
            activeList.remove(toArchive);
            writeFile(getFilePath(TELEMETRY_FILE), activeList);

            // 4. Carrega a lista de arquivados
            List<DroneTelemetry> archivedList = loadListFromFile(getFilePath(ARCHIVED_FILE), new TypeReference<List<DroneTelemetry>>() {});
            
            // 5. Adiciona e salva no arquivo de arquivo
            archivedList.add(toArchive);
            writeFile(getFilePath(ARCHIVED_FILE), archivedList);
            
            System.out.println("[JsonDatabaseService] Telemetria do Drone " + droneId + " movida para arquivo.");
        }
    }

    // --- Helpers Genéricos ---
    private synchronized <T> void writeFile(String filePath, List<T> data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized <T> List<T> loadListFromFile(String filePath, TypeReference<List<T>> typeRef) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                mapper.writeValue(file, new ArrayList<T>());
                return new ArrayList<>();
            }
            return mapper.readValue(file, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}