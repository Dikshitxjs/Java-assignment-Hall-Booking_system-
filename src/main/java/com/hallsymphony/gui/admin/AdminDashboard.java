package com.hallsymphony.gui.admin;

import com.hallsymphony.model.user.User;
import com.hallsymphony.model.user.Administrator;
import com.hallsymphony.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final Administrator admin;
    private final UserService userService;
    private final DefaultTableModel userModel;

    public AdminDashboard(Administrator admin, UserService userService) {
        super("Admin Dashboard - " + admin.getFullName());
        this.admin = admin;
        this.userService = userService;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        userModel = new DefaultTableModel(new String[]{"User ID", "Name", "Email", "Role", "Status"}, 0);
        JTable userTable = new JTable(userModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(userTable);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this::refreshUsers);

        JButton blockButton = new JButton("Block Selected User");
        blockButton.addActionListener(e -> blockSelectedUser(userTable));

        JButton staffBtn = new JButton("Manage Staff");
        staffBtn.addActionListener(e -> new StaffManagementFrame().setVisible(true));

        JButton bookingsBtn = new JButton("View All Bookings");
        bookingsBtn.addActionListener(e -> new BookingOverviewFrame().setVisible(true));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            com.hallsymphony.gui.login.LoginFrame.run();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(blockButton);
        buttonPanel.add(staffBtn);
        buttonPanel.add(bookingsBtn);
        buttonPanel.add(logoutButton);

        add(new JLabel("Admin Dashboard", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshUsers(null);
    }

    private void refreshUsers(ActionEvent event) {
        List<User> users = userService.getAllUsers();
        userModel.setRowCount(0);
        for (User u : users) {
            String role = u.getClass().getSimpleName();
            userModel.addRow(new Object[]{u.getUserId(), u.getFullName(), u.getEmail(), role, u.getStatus()});
        }
    }

    private void blockSelectedUser(JTable table) {
        int idx = table.getSelectedRow();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String userId = (String) userModel.getValueAt(idx, 0);
        if (userId == null || userId.isBlank()) {
            JOptionPane.showMessageDialog(this, "Invalid user selection.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userId.equals(admin.getUserId())) {
            JOptionPane.showMessageDialog(this, "You cannot block your own account.", "Action not allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        userService.blockUser(userId);
        refreshUsers(null);
        JOptionPane.showMessageDialog(this, "User blocked (if existed).", "Done", JOptionPane.INFORMATION_MESSAGE);
    }
}
