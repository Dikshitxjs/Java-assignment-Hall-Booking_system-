package com.hallsymphony.model.user;

public class Administrator extends Staff {

    public Administrator(String userId, String fullName, String email, String password, String status,
                         String staffId, String role, java.time.LocalDate joinedDate) {
        super(userId, fullName, email, password, status, staffId, role, joinedDate);
    }

    public void addScheduler() {
        // TODO: Add scheduler (via UserService)
    }

    public void updateScheduler() {
        // TODO: Update scheduler (via UserService)
    }

    public void removeScheduler() {
        // TODO: Remove scheduler (via UserService)
    }

    public void viewUsers() {
        // TODO: View users (via UserService)
    }

    public void blockUser() {
        // TODO: Block user (via UserService)
    }

    public void viewAllBookings() {
        // TODO: View bookings (via BookingService)
    }
}
