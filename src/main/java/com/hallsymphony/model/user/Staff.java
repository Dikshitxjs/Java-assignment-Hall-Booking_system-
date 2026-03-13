package com.hallsymphony.model.user;

import java.time.LocalDate;

public abstract class Staff extends User {
    protected String staffId;
    protected String role;
    protected LocalDate joinedDate;

    public Staff(String userId, String fullName, String email, String password, String status,
                 String staffId, String role, LocalDate joinedDate) {
        super(userId, fullName, email, password, status);
        this.staffId = staffId;
        this.role = role;
        this.joinedDate = joinedDate;
    }

    public void viewDashboard() {
        // TODO: Show dashboard UI or data
    }

    public String getStaffId() {
        return staffId;
    }

    public String getRole() {
        return role;
    }

    public java.time.LocalDate getJoinedDate() {
        return joinedDate;
    }
}
