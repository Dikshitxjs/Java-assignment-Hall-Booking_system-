package com.hallsymphony.model.user;

import java.time.LocalDate;

public class Customer extends User {
    private String phoneNumber;
    private String address;
    private LocalDate registrationDate;

    public Customer(String userId, String fullName, String email, String password, String status,
                    String phoneNumber, String address, LocalDate registrationDate) {
        super(userId, fullName, email, password, status);
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    public void register() {
        // TODO: Add registration logic here (persist to file)
    }

    public void viewAvailableHalls() {
        // TODO: Show available halls (via HallService)
    }

    public void createBooking() {
        // TODO: Create booking (via BookingService)
    }

    public void cancelBooking() {
        // TODO: Cancel booking (via BookingService)
    }

    public void makePayment() {
        // TODO: Make payment (via PaymentService)
    }

    public void viewReceipt() {
        // TODO: View receipt (via PaymentService)
    }

    public void raiseIssue() {
        // TODO: Raise issue (via IssueService)
    }

    // Getters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
}
