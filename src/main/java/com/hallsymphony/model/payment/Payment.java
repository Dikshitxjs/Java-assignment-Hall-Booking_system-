package com.hallsymphony.model.payment;

import java.time.LocalDate;

public class Payment {
    private String paymentId;
    private String bookingId;
    private double amount;
    private LocalDate paymentDate;
    private String paymentStatus;

    public Payment(String paymentId, String bookingId, double amount, LocalDate paymentDate, String paymentStatus) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
    }

    public boolean processPayment() {
        // TODO: implement payment gateway integration
        this.paymentStatus = "COMPLETED";
        return true;
    }

    public boolean validatePayment() {
        return amount > 0;
    }

    public Receipt generateReceipt() {
        return new Receipt(paymentId + "-R", this, LocalDate.now());
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentStatus() { return paymentStatus; }
}
