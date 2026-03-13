package com.hallsymphony.model.booking;

import java.time.LocalDateTime;

public class TimeSlot {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String remarks;

    public TimeSlot(LocalDateTime startDateTime, LocalDateTime endDateTime, String remarks) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.remarks = remarks;
    }

    public boolean overlaps(TimeSlot other) {
        return startDateTime.isBefore(other.endDateTime) && other.startDateTime.isBefore(endDateTime);
    }

    public boolean isValidSlot() {
        return startDateTime != null && endDateTime != null && startDateTime.isBefore(endDateTime);
    }

    // Getters
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public String getRemarks() { return remarks; }
}
