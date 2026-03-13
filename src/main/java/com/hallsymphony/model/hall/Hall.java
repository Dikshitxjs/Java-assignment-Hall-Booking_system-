package com.hallsymphony.model.hall;

public abstract class Hall {
    protected String hallId;
    protected String hallName;
    protected int capacity;
    protected double ratePerHour;
    protected String status;

    public Hall(String hallId, String hallName, int capacity, double ratePerHour, String status) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.capacity = capacity;
        this.ratePerHour = ratePerHour;
        this.status = status;
    }

    public double calculateCost(double hours) {
        return ratePerHour * hours;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public String getHallDetails() {
        return String.format("%s (%s) - capacity: %d - rate/hr: %.2f - status: %s",
                hallId, hallName, capacity, ratePerHour, status);
    }

    // Getters
    public String getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public int getCapacity() { return capacity; }
    public double getRatePerHour() { return ratePerHour; }
    public String getStatus() { return status; }
}
