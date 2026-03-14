package com.hallsymphony.gui.customer;

import com.hallsymphony.model.booking.Booking;
import com.hallsymphony.model.booking.BookingStatus;
import com.hallsymphony.model.hall.Hall;
import com.hallsymphony.model.payment.Payment;
import com.hallsymphony.model.user.Customer;
import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.HallService;
import com.hallsymphony.service.IssueService;
import com.hallsymphony.service.PaymentService;
import com.hallsymphony.util.IdGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
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

    // Keeps the latest bookings list shown in the table for quick lookup.
    private java.util.List<Booking> currentBookings;

    private final JComboBox<Hall> hallCombo;

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
        JPanel bookingPanel = new JPanel(new GridBagLayout());
        bookingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        hallCombo = new JComboBox<>();
        hallCombo.setToolTipText("Select a hall to book");
        hallCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Hall) {
                    Hall hall = (Hall) value;
                    setText(String.format("%s - %s (RM %.2f/hr)", hall.getHallId(), hall.getHallName(), hall.getRatePerHour()));
                }
                return this;
            }
        });

        JPanel hallSelectRow = new JPanel(new BorderLayout(5, 0));
        hallSelectRow.add(hallCombo, BorderLayout.CENTER);
        JButton refreshHallsButton = new JButton("Refresh");
        refreshHallsButton.addActionListener(this::refreshAvailableHalls);
        hallSelectRow.add(refreshHallsButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        bookingPanel.add(new JLabel("Select hall:"), gbc);
        gbc.gridx = 1;
        bookingPanel.add(hallSelectRow, gbc);

        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setToolTipText("Choose a booking date.");

        gbc.gridy++;
        gbc.gridx = 0;
        bookingPanel.add(new JLabel("Booking date:"), gbc);
        gbc.gridx = 1;
        bookingPanel.add(dateSpinner, gbc);

        java.util.Calendar defaultTime = java.util.Calendar.getInstance();
        defaultTime.set(java.util.Calendar.SECOND, 0);
        defaultTime.set(java.util.Calendar.MILLISECOND, 0);
        defaultTime.set(java.util.Calendar.HOUR_OF_DAY, 8);
        defaultTime.set(java.util.Calendar.MINUTE, 0);

        SpinnerDateModel startModel = new SpinnerDateModel(defaultTime.getTime(), null, null, java.util.Calendar.MINUTE);
        JSpinner startSpinner = new JSpinner(startModel);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "HH:mm"));
        startSpinner.setToolTipText("Start time (between 08:00 and 18:00).");

        java.util.Calendar endTime = (java.util.Calendar) defaultTime.clone();
        endTime.add(java.util.Calendar.HOUR_OF_DAY, 1);
        SpinnerDateModel endModel = new SpinnerDateModel(endTime.getTime(), null, null, java.util.Calendar.MINUTE);
        JSpinner endSpinner = new JSpinner(endModel);
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "HH:mm"));
        endSpinner.setToolTipText("End time (between 08:00 and 18:00). Must be after start time.");

        gbc.gridy++;
        gbc.gridx = 0;
        bookingPanel.add(new JLabel("Start time:"), gbc);
        gbc.gridx = 1;
        bookingPanel.add(startSpinner, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        bookingPanel.add(new JLabel("End time:"), gbc);
        gbc.gridx = 1;
        bookingPanel.add(endSpinner, gbc);

        JButton bookBtn = new JButton("Create Booking");
        bookBtn.setToolTipText("Create a booking based on the selected hall, date and time slot.");
        bookBtn.addActionListener(e -> createBooking((Hall) hallCombo.getSelectedItem(), (Date) dateSpinner.getValue(), (Date) startSpinner.getValue(), (Date) endSpinner.getValue()));

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        bookingPanel.add(bookBtn, gbc);

        tabs.addTab("Make Booking", bookingPanel);

        // My bookings tab
        bookingsModel = new DefaultTableModel(new String[]{"Booking ID", "Hall ID", "Date", "Start", "End", "Amount", "Status"}, 0);
        JTable bookingsTable = new JTable(bookingsModel);
        JScrollPane bookingsScroll = new JScrollPane(bookingsTable);
        JButton refreshBookings = new JButton("Refresh");
        refreshBookings.addActionListener(e -> refreshBookings());

        JButton payBooking = new JButton("Pay Selected");
        payBooking.addActionListener(e -> paySelectedBooking(bookingsTable));

        JButton cancelBooking = new JButton("Cancel Selected");
        cancelBooking.addActionListener(e -> cancelSelectedBooking(bookingsTable));

        JPanel bookingsPanel = new JPanel(new BorderLayout());
        JPanel bookingsButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bookingsButtons.add(refreshBookings);
        bookingsButtons.add(payBooking);
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
        hallCombo.removeAllItems();
        for (Hall h : halls) {
            hallsModel.addRow(new Object[]{h.getHallId(), h.getHallName(), h.getClass().getSimpleName(), h.getRatePerHour(), h.getStatus()});
            hallCombo.addItem(h);
        }
    }

    private void createBooking(Hall selectedHall, Date dateValue, Date startValue, Date endValue) {
        if (selectedHall == null || dateValue == null || startValue == null || endValue == null) {
            JOptionPane.showMessageDialog(this, "Please select a hall, date and time.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date = dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime start = startValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
        LocalTime end = endValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);

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

        Hall hall = selectedHall;
        long hours = java.time.Duration.between(start, end).toHours();
        double amount = hall.getRatePerHour() * hours;
        Booking booking = new Booking("B-" + UUID.randomUUID(), customer.getUserId(), hall.getHallId(), date, start, end, amount, BookingStatus.PENDING);
        Booking created = bookingService.createBooking(booking);
        if (created == null) {
            JOptionPane.showMessageDialog(this, "Hall is already booked for that slot.", "Conflict", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Save payment record
        String paymentId = IdGenerator.generatePaymentId();
        Payment payment = new Payment(paymentId, booking.getBookingId(), amount, LocalDate.now(), "PENDING");
        paymentService.addPayment(payment);

        JOptionPane.showMessageDialog(this, "Booking created. Total: RM " + amount, "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshBookings();
    }

    private void refreshBookings() {
        // Keep currentBookings in sync with the table rows (index-based lookup)
        currentBookings = bookingService.getBookingsForCustomer(customer.getUserId());
        bookingsModel.setRowCount(0);
        for (Booking b : currentBookings) {
            bookingsModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getHallId(),
                    b.getBookingDate(),
                    b.getStartTime(),
                    b.getEndTime(),
                    b.getTotalAmount(),
                    b.getBookingStatus()});
        }
    }

    private void cancelSelectedBooking(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = currentBookings.get(row);
        boolean success = bookingService.cancelBooking(booking.getBookingId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to cancel booking (may be within 3 days or already cancelled).", "Failure", JOptionPane.WARNING_MESSAGE);
        }
        refreshBookings();
    }

    private void paySelectedBooking(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Booking booking = currentBookings.get(row);
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending bookings can be paid.", "Invalid action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new PaymentFrame(booking, hallService, paymentService, bookingService).setVisible(true);
        refreshBookings();
    }

    private void raiseIssue(String bookingId, String description) {
        if (bookingId == null || bookingId.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Booking ID and description are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        issueService.raiseIssue(new com.hallsymphony.model.issue.Issue(
                "I-" + UUID.randomUUID(),
                bookingId,
                description,
                LocalDate.now(),
                com.hallsymphony.model.issue.IssueStatus.IN_PROGRESS));

        JOptionPane.showMessageDialog(this, "Issue registered.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }
}
