package com.hallsymphony.service;

import com.hallsymphony.model.booking.Booking;
import com.hallsymphony.model.payment.Payment;
import com.hallsymphony.model.payment.Receipt;
import com.hallsymphony.util.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PaymentService {
    private static final Path PAYMENT_FILE = Paths.get("data", "payments.txt");

    public PaymentService() {
        ensureDataFiles();
    }

    private void ensureDataFiles() {
        try {
            if (Files.notExists(PAYMENT_FILE.getParent())) {
                Files.createDirectories(PAYMENT_FILE.getParent());
            }
            if (Files.notExists(PAYMENT_FILE)) {
                Files.write(PAYMENT_FILE, List.of("# Payment data file"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Payment> parsePayment(String line) {
        if (line == null || line.isBlank() || line.startsWith("#")) {
            return Optional.empty();
        }
        String[] parts = line.split("\\|");
        if (parts.length < 5) {
            return Optional.empty();
        }
        String paymentId = parts[0].trim();
        String bookingId = parts[1].trim();
        double amount = Double.parseDouble(parts[2].trim());
        LocalDate date = LocalDate.parse(parts[3].trim());
        String status = parts[4].trim();
        return Optional.of(new Payment(paymentId, bookingId, amount, date, status));
    }

    private String paymentToLine(Payment payment) {
        return String.join("|",
                payment.getPaymentId(),
                payment.getBookingId(),
                String.valueOf(payment.getAmount()),
                payment.getPaymentDate().toString(),
                payment.getPaymentStatus());
    }

    public boolean processPayment(Payment payment) {
        if (payment == null || !payment.validatePayment()) {
            return false;
        }
        payment.processPayment();
        try {
            List<String> lines = FileHandler.readFromFile(PAYMENT_FILE);
            lines.add(paymentToLine(payment));
            FileHandler.writeToFile(PAYMENT_FILE, lines);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Receipt generateReceipt(Payment payment) {
        return payment.generateReceipt();
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        try {
            List<String> lines = FileHandler.readFromFile(PAYMENT_FILE);
            for (String line : lines) {
                parsePayment(line).ifPresent(payments::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public List<Payment> getPaymentsForBooking(String bookingId) {
        List<Payment> payments = new ArrayList<>();
        try {
            List<String> lines = FileHandler.readFromFile(PAYMENT_FILE);
            for (String line : lines) {
                Optional<Payment> opt = parsePayment(line);
                if (opt.isPresent() && opt.get().getBookingId().equals(bookingId)) {
                    payments.add(opt.get());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public void addPayment(Payment payment) {
        try {
            List<String> lines = FileHandler.readFromFile(PAYMENT_FILE);
            lines.add(paymentToLine(payment));
            FileHandler.writeToFile(PAYMENT_FILE, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getTotalSalesThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
        int currentYear = today.get(weekFields.weekBasedYear());

        double total = 0;
        for (Payment p : getAllPayments()) {
            if ("COMPLETED".equalsIgnoreCase(p.getPaymentStatus())) {
                LocalDate payDate = p.getPaymentDate();
                int payWeek = payDate.get(weekFields.weekOfWeekBasedYear());
                int payYear = payDate.get(weekFields.weekBasedYear());
                if (payWeek == currentWeek && payYear == currentYear) {
                    total += p.getAmount();
                }
            }
        }
        return total;
    }

    public double getTotalSalesThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        double total = 0;
        for (Payment p : getAllPayments()) {
            if ("COMPLETED".equalsIgnoreCase(p.getPaymentStatus())) {
                YearMonth payMonth = YearMonth.from(p.getPaymentDate());
                if (payMonth.equals(currentMonth)) {
                    total += p.getAmount();
                }
            }
        }
        return total;
    }

    public double getTotalSalesThisYear() {
        int currentYear = LocalDate.now().getYear();

        double total = 0;
        for (Payment p : getAllPayments()) {
            if ("COMPLETED".equalsIgnoreCase(p.getPaymentStatus())) {
                if (p.getPaymentDate().getYear() == currentYear) {
                    total += p.getAmount();
                }
            }
        }
        return total;
    }

    public double getTotalSalesAllTime() {
        double total = 0;
        for (Payment p : getAllPayments()) {
            if ("COMPLETED".equalsIgnoreCase(p.getPaymentStatus())) {
                total += p.getAmount();
            }
        }
        return total;
    }
}
