package com.hallsymphony.controller;

import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.UserService;

public class AdminController {
    private final UserService userService = new UserService();
    private final BookingService bookingService = new BookingService();

    public void manageUsers() {
        // TODO: add/update/block users
    }

    public void manageStaff() {
        // TODO: add/update/remove staff
    }

    public void viewBookings() {
        // TODO: view all bookings
    }
}
