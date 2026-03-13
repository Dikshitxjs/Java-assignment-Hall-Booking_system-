package com.hallsymphony.model.hall;

public class Auditorium extends Hall {

    public Auditorium(String hallId, String hallName, int capacity, double ratePerHour, String status) {
        super(hallId, hallName, capacity, ratePerHour, status);
    }

    // Auditorium-specific behavior could be added here
}
