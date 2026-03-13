package com.hallsymphony.gui.admin;

import com.hallsymphony.model.booking.Booking;
import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.HallService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingOverviewFrame extends JFrame {
    private final BookingService bookingService;
    private final DefaultTableModel tableModel;

    public BookingOverviewFrame() {
        super("All Bookings Overview");
        this.bookingService = new BookingService();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Booking ID", "Customer ID", "Hall ID", "Date", "Start", "End", "Amount", "Status"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"});
        filterCombo.addActionListener(e -> filterByStatus((String) filterCombo.getSelectedItem()));

        buttonPanel.add(new JLabel("Filter by Status:"));
        buttonPanel.add(filterCombo);
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingService.getAllBookings();
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getCustomerId(),
                    b.getHallId(),
                    b.getBookingDate(),
                    b.getStartTime(),
                    b.getEndTime(),
                    String.format("%.2f", b.getTotalAmount()),
                    b.getBookingStatus()
            });
        }
    }

    private void filterByStatus(String status) {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingService.getAllBookings();
        for (Booking b : bookings) {
            if (status.equals("All") || b.getBookingStatus().toString().equals(status)) {
                tableModel.addRow(new Object[]{
                        b.getBookingId(),
                        b.getCustomerId(),
                        b.getHallId(),
                        b.getBookingDate(),
                        b.getStartTime(),
                        b.getEndTime(),
                        String.format("%.2f", b.getTotalAmount()),
                        b.getBookingStatus()
                });
            }
        }
    }
}
