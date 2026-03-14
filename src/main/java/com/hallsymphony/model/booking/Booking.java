package com.hallsymphony.model.booking;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a single hall booking.
 * <p>
 * This is a mostly immutable model; only the booking status can change after creation.
 * </p>
 */
public class Booking {
    /** Unique booking identifier */
    private final String bookingId;
    /** User who made the booking */
    private final String customerId;
    /** Hall that was booked */
    private final String hallId;
    /** Date of the booking */
    private final LocalDate bookingDate;
    /** Booking start time */
    private final LocalTime startTime;
    /** Booking end time */
    private final LocalTime endTime;
    /** Total amount charged for the booking */
    private final double totalAmount;
    /** Current booking status (pending/confirmed/cancelled) */
    private BookingStatus bookingStatus;

    public Booking(String bookingId, String customerId, String hallId,
                   LocalDate bookingDate, LocalTime startTime, LocalTime endTime,
                   double totalAmount, BookingStatus bookingStatus) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.hallId = hallId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
    }

    public long calculateDuration() {
        return java.time.Duration.between(startTime, endTime).toHours();
    }

    public double calculateTotalAmount(double ratePerHour) {
        return calculateDuration() * ratePerHour;
    }

    public void confirmBooking() {
        this.bookingStatus = BookingStatus.CONFIRMED;
    }

    public void cancelBooking() {
        this.bookingStatus = BookingStatus.CANCELLED;
    }

    public boolean isCancellable() {
        if (bookingStatus != BookingStatus.PENDING && bookingStatus != BookingStatus.CONFIRMED) {
            return false;
        }
        // Must cancel at least 3 days before the booking date
        LocalDate cutoff = LocalDate.now().plusDays(3);
        return !bookingDate.isBefore(cutoff);
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public String getHallId() { return hallId; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public double getTotalAmount() { return totalAmount; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
}
