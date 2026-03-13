package com.hallsymphony.controller;

import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.IssueService;
import com.hallsymphony.service.PaymentService;

public class CustomerController {
    private final BookingService bookingService = new BookingService();
    private final PaymentService paymentService = new PaymentService();
    private final IssueService issueService = new IssueService();

    public void handleBooking() {
        // TODO: orchestrate booking creation
    }

    public void handlePayment() {
        // TODO: orchestrate payment processing
    }

    public void handleIssueSubmission() {
        // TODO: orchestrate issue submission
    }
}
