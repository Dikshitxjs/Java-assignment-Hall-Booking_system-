package com.hallsymphony.service;

import com.hallsymphony.model.hall.Auditorium;
import com.hallsymphony.model.hall.BanquetHall;
import com.hallsymphony.model.hall.Hall;
import com.hallsymphony.model.hall.MeetingRoom;
import com.hallsymphony.util.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HallService {
    private static final Path HALL_FILE = Paths.get("data", "halls.txt");

    public HallService() {
        ensureDataFiles();
    }

    private void ensureDataFiles() {
        try {
            if (Files.notExists(HALL_FILE.getParent())) {
                Files.createDirectories(HALL_FILE.getParent());
            }
            if (Files.notExists(HALL_FILE)) {
                Files.write(HALL_FILE, List.of("# Hall data file"));
            }

            List<String> lines = FileHandler.readFromFile(HALL_FILE);
            long dataLines = lines.stream().filter(l -> l != null && !l.isBlank() && !l.startsWith("#")).count();
            if (dataLines == 0) {
                addHall(new Auditorium("H-AUD-1", "Auditorium 1", 1000, 300.0, "AVAILABLE"));
                addHall(new BanquetHall("H-BAN-1", "Banquet Hall 1", 300, 100.0, "AVAILABLE"));
                addHall(new MeetingRoom("H-MTG-1", "Meeting Room 1", 30, 50.0, "AVAILABLE"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Hall> parseHall(String line) {
        if (line == null || line.isBlank() || line.startsWith("#")) {
            return Optional.empty();
        }
        String[] parts = line.split("\\|");
        if (parts.length < 6) {
            return Optional.empty();
        }
        String hallId = parts[0].trim();
        String hallType = parts[1].trim();
        String hallName = parts[2].trim();
        int capacity = Integer.parseInt(parts[3].trim());
        double rate = Double.parseDouble(parts[4].trim());
        String status = parts[5].trim();

        switch (hallType.toUpperCase()) {
            case "AUDITORIUM":
                return Optional.of(new Auditorium(hallId, hallName, capacity, rate, status));
            case "BANQUETHALL":
            case "BANQUET":
            case "BANQUET HALL":
                return Optional.of(new BanquetHall(hallId, hallName, capacity, rate, status));
            case "MEETINGROOM":
            case "MEETING ROOM":
                return Optional.of(new MeetingRoom(hallId, hallName, capacity, rate, status));
            default:
                return Optional.empty();
        }
    }

    private String hallToLine(Hall hall) {
        String type;
        if (hall instanceof Auditorium) {
            type = "AUDITORIUM";
        } else if (hall instanceof BanquetHall) {
            type = "BANQUET";
        } else if (hall instanceof MeetingRoom) {
            type = "MEETINGROOM";
        } else {
            type = "UNKNOWN";
        }
        return String.join("|", hall.getHallId(), type, hall.getHallName(), String.valueOf(hall.getCapacity()),
                String.valueOf(hall.getRatePerHour()), hall.getStatus());
    }

    public void addHall(Hall hall) {
        try {
            List<String> lines = FileHandler.readFromFile(HALL_FILE);
            lines.add(hallToLine(hall));
            FileHandler.writeToFile(HALL_FILE, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateHall(Hall hall) {
        try {
            List<String> lines = FileHandler.readFromFile(HALL_FILE);
            for (int i = 0; i < lines.size(); i++) {
                Optional<Hall> opt = parseHall(lines.get(i));
                if (opt.isPresent() && opt.get().getHallId().equals(hall.getHallId())) {
                    lines.set(i, hallToLine(hall));
                    FileHandler.writeToFile(HALL_FILE, lines);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteHall(String hallId) {
        try {
            List<String> lines = FileHandler.readFromFile(HALL_FILE);
            for (int i = 0; i < lines.size(); i++) {
                Optional<Hall> opt = parseHall(lines.get(i));
                if (opt.isPresent() && opt.get().getHallId().equals(hallId)) {
                    lines.remove(i);
                    FileHandler.writeToFile(HALL_FILE, lines);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Hall> getAllHalls() {
        List<Hall> halls = new ArrayList<>();
        try {
            List<String> lines = FileHandler.readFromFile(HALL_FILE);
            for (String line : lines) {
                parseHall(line).ifPresent(halls::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return halls;
    }

    public List<Hall> getAvailableHalls() {
        List<Hall> available = new ArrayList<>();
        for (Hall hall : getAllHalls()) {
            if ("AVAILABLE".equalsIgnoreCase(hall.getStatus())) {
                available.add(hall);
            }
        }
        return available;
    }
}
