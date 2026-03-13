package com.hallsymphony.model.user;

public class Scheduler extends Staff {

    public Scheduler(String userId, String fullName, String email, String password, String status,
                     String staffId, String role, java.time.LocalDate joinedDate) {
        super(userId, fullName, email, password, status, staffId, role, joinedDate);
    }

    public void addHall() {
        // TODO: Add hall (via HallService)
    }

    public void updateHall() {
        // TODO: Update hall (via HallService)
    }

    public void deleteHall() {
        // TODO: Delete hall (via HallService)
    }

    public void viewHalls() {
        // TODO: View halls (via HallService)
    }

    public void setHallAvailability() {
        // TODO: Update hall availability
    }

    public void setMaintenanceSchedule() {
        // TODO: Schedule maintenance
    }
}
