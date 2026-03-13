package com.hallsymphony.model.hall;

public class MeetingRoom extends Hall {

    public MeetingRoom(String hallId, String hallName, int capacity, double ratePerHour, String status) {
        super(hallId, hallName, capacity, ratePerHour, status);
    }

    // MeetingRoom-specific behavior could be added here
}
