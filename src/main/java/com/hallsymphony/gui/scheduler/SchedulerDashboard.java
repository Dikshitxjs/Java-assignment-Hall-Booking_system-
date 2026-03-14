package com.hallsymphony.gui.scheduler;

import com.hallsymphony.model.hall.Hall;
import com.hallsymphony.model.hall.Auditorium;
import com.hallsymphony.model.hall.BanquetHall;
import com.hallsymphony.model.hall.MeetingRoom;
import com.hallsymphony.model.user.Scheduler;
import com.hallsymphony.service.HallService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.UUID;

public class SchedulerDashboard extends JFrame {
    private final Scheduler scheduler;
    private final HallService hallService;
    private final DefaultTableModel hallModel;

    public SchedulerDashboard(Scheduler scheduler, HallService hallService) {
        super("Scheduler Dashboard - " + scheduler.getFullName());
        this.scheduler = scheduler;
        this.hallService = hallService;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        hallModel = new DefaultTableModel(new String[]{"Hall ID", "Name", "Type", "Capacity", "Rate/hr", "Status"}, 0);
        JTable hallTable = new JTable(hallModel);
        hallTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScroll = new JScrollPane(hallTable);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this::refreshHalls);

        JButton addButton = new JButton("Add Hall");
        addButton.addActionListener(e -> showAddHallDialog());

        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.addActionListener(e -> updateSelectedStatus(hallTable));

        JButton deleteButton = new JButton("Delete Hall");
        deleteButton.addActionListener(e -> deleteSelectedHall(hallTable));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new com.hallsymphony.gui.login.LoginFrame().run();
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controls.add(refreshButton);
        controls.add(addButton);
        controls.add(updateStatusButton);
        controls.add(deleteButton);
        controls.add(logoutButton);

        add(new JLabel("Scheduler Dashboard", SwingConstants.CENTER), BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);

        refreshHalls(null);
    }

    private void refreshHalls(ActionEvent event) {
        List<Hall> halls = hallService.getAllHalls();
        hallModel.setRowCount(0);
        for (Hall h : halls) {
            hallModel.addRow(new Object[]{h.getHallId(), h.getHallName(), h.getClass().getSimpleName(), h.getCapacity(), h.getRatePerHour(), h.getStatus()});
        }
    }

    private void showAddHallDialog() {
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField rateField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Hall type (AUDITORIUM/BANQUET/MEETING):"));
        panel.add(typeField);
        panel.add(new JLabel("Hall name:"));
        panel.add(nameField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacityField);
        panel.add(new JLabel("Rate per hour:"));
        panel.add(rateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Hall", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String type = typeField.getText().trim().toUpperCase();
        String name = nameField.getText().trim();
        int capacity;
        double rate;
        try {
            capacity = Integer.parseInt(capacityField.getText().trim());
            rate = Double.parseDouble(rateField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be an integer and rate must be a number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hall hall;
        if (type.startsWith("AUD")) {
            hall = new Auditorium("H-" + UUID.randomUUID(), name, capacity, rate, "AVAILABLE");
        } else if (type.startsWith("BAN")) {
            hall = new BanquetHall("H-" + UUID.randomUUID(), name, capacity, rate, "AVAILABLE");
        } else {
            hall = new MeetingRoom("H-" + UUID.randomUUID(), name, capacity, rate, "AVAILABLE");
        }

        hallService.addHall(hall);
        refreshHalls(null);
        JOptionPane.showMessageDialog(this, "Hall added.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSelectedStatus(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a hall first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String hallId = (String) hallModel.getValueAt(row, 0);
        String status = JOptionPane.showInputDialog(this, "New status (AVAILABLE/UNAVAILABLE):", "Update Status", JOptionPane.PLAIN_MESSAGE);
        if (status == null || status.trim().isEmpty()) {
            return;
        }
        List<Hall> halls = hallService.getAllHalls();
        for (Hall h : halls) {
            if (h.getHallId().equals(hallId)) {
                h.updateStatus(status.trim().toUpperCase());
                hallService.updateHall(h);
                refreshHalls(null);
                JOptionPane.showMessageDialog(this, "Hall status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Hall not found.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteSelectedHall(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a hall first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String hallId = (String) hallModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete hall " + hallId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        hallService.deleteHall(hallId);
        refreshHalls(null);
        JOptionPane.showMessageDialog(this, "Hall deleted.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }
}
