package com.hallsymphony.gui.customer;

import com.hallsymphony.model.booking.Booking;
import com.hallsymphony.model.booking.BookingStatus;
import com.hallsymphony.model.hall.Hall;
import com.hallsymphony.model.payment.Payment;
import com.hallsymphony.model.payment.Receipt;
import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.HallService;
import com.hallsymphony.service.PaymentService;
import com.hallsymphony.util.IdGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;

public class PaymentFrame extends JFrame {
    private final Booking booking;
    private final HallService hallService;
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private Hall hall;

    public PaymentFrame(Booking booking, HallService hallService, PaymentService paymentService, BookingService bookingService) {
        super("Process Payment");
        this.booking = booking;
        this.hallService = hallService;
        this.paymentService = paymentService;
        this.bookingService = bookingService;

        for (Hall h : hallService.getAllHalls()) {
            if (h.getHallId().equals(booking.getHallId())) {
                this.hall = h;
                break;
            }
        }

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Payment Processing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addDetailRow(detailsPanel, "Booking ID:", booking.getBookingId());
        addDetailRow(detailsPanel, "Hall Name:", hall != null ? hall.getHallName() : "Unknown");
        addDetailRow(detailsPanel, "Booking Date:", booking.getBookingDate().toString());
        addDetailRow(detailsPanel, "Start Time:", booking.getStartTime().toString());
        addDetailRow(detailsPanel, "End Time:", booking.getEndTime().toString());

        detailsPanel.add(Box.createVerticalStrut(20));

        JLabel amountLabel = new JLabel("Amount to Pay: RM " + String.format("%.2f", booking.getTotalAmount()));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setForeground(new Color(52, 152, 219));
        detailsPanel.add(amountLabel);

        detailsPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton payBtn = new JButton("Pay Now");
        payBtn.setFont(new Font("Arial", Font.BOLD, 14));
        payBtn.setBackground(new Color(46, 204, 113));
        payBtn.setForeground(Color.WHITE);
        payBtn.setPreferredSize(new Dimension(150, 45));
        payBtn.setBorderPainted(false);
        payBtn.setFocusPainted(false);
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payBtn.addActionListener(e -> processPayment());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(new Color(80, 80, 80));
        cancelBtn.setPreferredSize(new Dimension(150, 45));
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(payBtn);
        buttonPanel.add(cancelBtn);
        detailsPanel.add(buttonPanel);

        mainPanel.add(detailsPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setPreferredSize(new Dimension(130, 25));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));

        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
    }

    private void processPayment() {
        try {
            Optional<Payment> existingPayment = paymentService.getPaymentByBookingId(booking.getBookingId());
            Payment payment;
            if (existingPayment.isPresent()) {
                paymentService.updatePaymentStatus(existingPayment.get().getPaymentId(), "COMPLETED");
                payment = existingPayment.get();
            } else {
                // Fallback, but should not happen
                String paymentId = IdGenerator.generatePaymentId();
                payment = new Payment(
                        paymentId,
                        booking.getBookingId(),
                        booking.getTotalAmount(),
                        LocalDate.now(),
                        "COMPLETED"
                );
                paymentService.addPayment(payment);
            }

            Booking confirmedBooking = new Booking(
                    booking.getBookingId(),
                    booking.getCustomerId(),
                    booking.getHallId(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getTotalAmount(),
                    BookingStatus.CONFIRMED
            );
            bookingService.updateBooking(confirmedBooking);

            JOptionPane.showMessageDialog(this, "Payment processed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            new ReceiptFrame(payment, booking, hall).setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Payment processing failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
