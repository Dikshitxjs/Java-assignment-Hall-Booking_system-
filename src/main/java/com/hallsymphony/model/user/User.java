package com.hallsymphony.model.user;

public abstract class User {
    protected String userId;
    protected String fullName;
    protected String email;
    protected String password;
    protected String status;

    public User(String userId, String fullName, String email, String password, String status) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void logout() {
        // Placeholder for logout logic
    }

    public void updateProfile(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public boolean validateCredentials() {
        return email != null && password != null && !email.isEmpty() && !password.isEmpty();
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}
