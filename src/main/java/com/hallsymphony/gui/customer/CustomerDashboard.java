package com.hallsymphony.gui.customer;

import com.hallsymphony.model.booking.Booking;
import com.hallsymphony.model.booking.BookingStatus;
import com.hallsymphony.model.hall.Hall;
import com.hallsymphony.model.user.Customer;
import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.HallService;
import com.hallsymphony.service.IssueService;
import com.hallsymphony.service.PaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class CustomerDashboard extends JFrame {
    private final Customer customer;
    private final HallService hallService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final IssueService issueService;
    private final DefaultTableModel hallsModel;
    private final DefaultTableModel bookingsModel;

    public CustomerDashboard(Customer customer, HallService hallService, BookingService bookingService,
                             PaymentService paymentService, IssueService issueService) {
        super("Customer Dashboard - " + customer.getFullName());
        this.customer = customer;
        this.hallService = hallService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.issueService = issueService;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Available halls tab
        hallsModel = new DefaultTableModel(new String[]{"Hall ID", "Name", "Type", "Rate/hr", "Status"}, 0);
        JTable hallsTable = new JTable(hallsModel);
        JScrollPane hallsScroll = new JScrollPane(hallsTable);
        JButton refreshHalls = new JButton("Refresh");
        refreshHalls.addActionListener(this::refreshAvailableHalls);
        JPanel hallPanel = new JPanel(new BorderLayout());
        hallPanel.add(hallsScroll, BorderLayout.CENTER);
        hallPanel.add(refreshHalls, BorderLayout.SOUTH);
        tabs.addTab("Available Halls", hallPanel);

        // Booking tab
        JPanel bookingPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField hallIdField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField startField = new JTextField("HH:MM");
        JTextField endField = new JTextField("HH:MM");
        bookingPanel.add(new JLabel("Hall ID:"));
        bookingPanel.add(hallIdField);
        bookingPanel.add(new JLabel("Booking date:"));
        bookingPanel.add(dateField);
        bookingPanel.add(new JLabel("Start time:"));
        bookingPanel.add(startField);
        bookingPanel.add(new JLabel("End time:"));
        bookingPanel.add(endField);
        JButton bookBtn = new JButton("Create Booking");
        bookBtn.addActionListener(e -> createBooking(hallIdField.getText().trim(), dateField.getText().trim(), startField.getText().trim(), endField.getText().trim()));
        bookingPanel.add(new JLabel());
        bookingPanel.add(bookBtn);
        tabs.addTab("Make Booking", bookingPanel);

        // My bookings tab
        bookingsModel = new DefaultTableModel(new String[]{"Booking ID", "Hall ID", "Date", "Start", "End", "Amount", "Status"}, 0);
        JTable bookingsTable = new JTable(bookingsModel);
        JScrollPane bookingsScroll = new JScrollPane(bookingsTable);
        JButton refreshBookings = new JButton("Refresh");
        refreshBookings.addActionListener(e -> refreshBookings());
        JButton cancelBooking = new JButton("Cancel Selected");
        cancelBooking.addActionListener(e -> cancelSelectedBooking(bookingsTable));
        JPanel bookingsPanel = new JPanel(new BorderLayout());
        JPanel bookingsButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bookingsButtons.add(refreshBookings);
        bookingsButtons.add(cancelBooking);
        bookingsPanel.add(bookingsScroll, BorderLayout.CENTER);
        bookingsPanel.add(bookingsButtons, BorderLayout.SOUTH);
        tabs.addTab("My Bookings", bookingsPanel);

        // Issue tab
        JPanel issuePanel = new JPanel(new GridLayout(0, 2, 8, 8));
        issuePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField issueBookingId = new JTextField();
        JTextArea issueDesc = new JTextArea(4, 20);
        issuePanel.add(new JLabel("Booking ID:"));
        issuePanel.add(issueBookingId);
        issuePanel.add(new JLabel("Issue description:"));
        issuePanel.add(new JScrollPane(issueDesc));
        JButton raiseIssueBtn = new JButton("Raise Issue");
        raiseIssueBtn.addActionListener(e -> raiseIssue(issueBookingId.getText().trim(), issueDesc.getText().trim()));
        issuePanel.add(new JLabel());
        issuePanel.add(raiseIssueBtn);
        tabs.addTab("Raise Issue", issuePanel);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new com.hallsymphony.gui.login.LoginFrame().run();
        });

        JButton profileBtn = new JButton("Update Profile");
        profileBtn.addActionListener(e -> {
            new ProfileFrame(customer).setVisible(true);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.add(profileBtn);
        bottom.add(logout);

        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        refreshAvailableHalls(null);
        refreshBookings();
    }

    private void refreshAvailableHalls(ActionEvent evt) {
        List<Hall> halls = hallService.getAvailableHalls();
        hallsModel.setRowCount(0);
        for (Hall h : halls) {
            hallsModel.addRow(new Object[]{h.getHallId(), h.getHallName(), h.getClass().getSimpleName(), h.getRatePerHour(), h.getStatus()});
        }
    }

    private void createBooking(String hallId, String dateText, String startText, String endText) {
        if (hallId.isBlank() || dateText.isBlank() || startText.isBlank() || endText.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            LocalDate date = LocalDate.parse(dateText);
            LocalTime start = LocalTime.parse(startText);
            LocalTime end = LocalTime.parse(endText);

            if (!bookingService.validateBookingDate(date)) {
                JOptionPane.showMessageDialog(this, "Booking date is outside allowed range.", "Invalid date", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalTime open = LocalTime.of(8, 0);
            LocalTime close = LocalTime.of(18, 0);
            if (start.isBefore(open) || end.isAfter(close) || !end.isAfter(start)) {
                JOptionPane.showMessageDialog(this, "Time must be between 08:00 and 18:00, and end after start.", "Invalid time", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Hall hall = hallService.getAllHalls().stream().filter(h -> h.getHallId().equals(hallId)).findFirst().orElse(null);
            if (hall == null) {
                JOptionPane.showMessageDialog(this, "Hall not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long hours = java.time.Duration.between(start, end).toHours();
            double amount = hall.getRatePerHour() * hours;
            Booking booking = new Booking("B-" + UUID.randomUUID(), customer.getUserId(), hallId, date, start, end, amount, BookingStatus.PENDING);
            Booking created = bookingService.createBooking(booking);
            if (created == null) {
                JOptionPane.showMessageDialog(this, "Hall is already booked for that slot.", "Conflict", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Booking created. Total: RM " + amount, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshBookings();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshBookings() {
        List<Booking> bookings = bookingService.getBookingsForCustomer(customer.getUserId());
        bookingsModel.setRowCount(0);
        for (Booking b : bookings) {
            bookingsModel.addRow(new Object[]{b.getBookingId(), b.getHallId(), b.getBookingDate(), b.getStartTime(), b.getEndTime(), b.getTotalAmount(), b.getBookingStatus()});
        }
    }

    private void cancelSelectedBooking(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String bookingId = (String) bookingsModel.getValueAt(row, 0);
        boolean success = bookingService.cancelBooking(bookingId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to cancel booking (may be within 3 days or already cancelled).", "Failure", JOptionPane.WARNING_MESSAGE);
        }
        refreshBookings();
    }

    private void raiseIssue(String bookingId, String description) {
        if (bookingId.isBlank() || description.isBlank()) {
            JOptionPane.showMessageDialog(this, "Booking ID and description are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        issueService.raiseIssue(new com.hallsymphony.model.issue.Issue("I-" + UUID.randomUUID(), bookingId, description, LocalDate.now(), com.hallsymphony.model.issue.IssueStatus.IN_PROGRESS));
        JOptionPane.showMessageDialog(this, "Issue registered.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }
}
