package com.hallsymphony.service;

import com.hallsymphony.model.booking.Booking;
import com.hallsymphony.model.booking.BookingStatus;
import com.hallsymphony.util.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingService {
    private static final Path BOOKING_FILE = Paths.get("data", "bookings.txt");

    public BookingService() {
        ensureDataFiles();
    }

    private void ensureDataFiles() {
        try {
            if (Files.notExists(BOOKING_FILE.getParent())) {
                Files.createDirectories(BOOKING_FILE.getParent());
            }
            if (Files.notExists(BOOKING_FILE)) {
                Files.write(BOOKING_FILE, List.of("# Booking data file"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Booking> parseBooking(String line) {
        if (line == null || line.isBlank() || line.startsWith("#")) {
            return Optional.empty();
        }
        String[] parts = line.split("\\|");
        if (parts.length < 8) {
            return Optional.empty();
        }
        String bookingId = parts[0].trim();
        String customerId = parts[1].trim();
        String hallId = parts[2].trim();
        LocalDate date = LocalDate.parse(parts[3].trim());
        LocalTime start = LocalTime.parse(parts[4].trim());
        LocalTime end = LocalTime.parse(parts[5].trim());
        double amount = Double.parseDouble(parts[6].trim());
        BookingStatus status = BookingStatus.valueOf(parts[7].trim());
        return Optional.of(new Booking(bookingId, customerId, hallId, date, start, end, amount, status));
    }

    private String bookingToLine(Booking booking) {
        return String.join("|",
                booking.getBookingId(),
                booking.getCustomerId(),
                booking.getHallId(),
                booking.getBookingDate().toString(),
                booking.getStartTime().toString(),
                booking.getEndTime().toString(),
                String.valueOf(booking.getTotalAmount()),
                booking.getBookingStatus().name());
    }

    public Booking createBooking(Booking booking) {
        try {
            if (!isHallAvailable(booking.getHallId(), booking.getBookingDate(), booking.getStartTime(), booking.getEndTime())) {
                return null;
            }
            List<String> lines = FileHandler.readFromFile(BOOKING_FILE);
            lines.add(bookingToLine(booking));
            FileHandler.writeToFile(BOOKING_FILE, lines);
            return booking;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean cancelBooking(String bookingId) {
        try {
            List<String> lines = FileHandler.readFromFile(BOOKING_FILE);
            for (int i = 0; i < lines.size(); i++) {
                Optional<Booking> opt = parseBooking(lines.get(i));
                if (opt.isPresent() && opt.get().getBookingId().equals(bookingId)) {
                    Booking booking = opt.get();
                    if (!booking.isCancellable()) {
                        return false;
                    }
                    booking.cancelBooking();
                    lines.set(i, bookingToLine(booking));
                    FileHandler.writeToFile(BOOKING_FILE, lines);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isHallAvailable(String hallId, LocalDate date, LocalTime start, LocalTime end) {
        for (Booking booking : getAllBookings()) {
            if (!booking.getHallId().equals(hallId) || booking.getBookingDate().isEqual(date) == false) {
                continue;
            }
            if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                continue;
            }
            // Check overlap
            if (start.isBefore(booking.getEndTime()) && end.isAfter(booking.getStartTime())) {
                return false;
            }
        }
        return true;
    }

    public List<Booking> getBookingsForCustomer(String customerId) {
        List<Booking> bookings = new ArrayList<>();
        try {
            List<String> lines = FileHandler.readFromFile(BOOKING_FILE);
            for (String line : lines) {
                Optional<Booking> opt = parseBooking(line);
                if (opt.isPresent() && opt.get().getCustomerId().equals(customerId)) {
                    bookings.add(opt.get());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try {
            List<String> lines = FileHandler.readFromFile(BOOKING_FILE);
            for (String line : lines) {
                parseBooking(line).ifPresent(bookings::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean validateBookingDate(LocalDate date) {
        // Only allow bookings within the next year
        LocalDate today = LocalDate.now();
        return !date.isBefore(today) && !date.isAfter(today.plusYears(1));
    }
}
