package com.hallsymphony.gui.admin;

import com.hallsymphony.model.user.Scheduler;
import com.hallsymphony.service.UserService;
import com.hallsymphony.util.IdGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StaffManagementFrame extends JFrame {
    private final UserService userService;
    private final DefaultTableModel tableModel;
    private final JTable staffTable;

    public StaffManagementFrame() {
        super("Manage Scheduler Staff");
        this.userService = new UserService();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Staff ID", "Name", "Email", "Role", "Status"}, 0);
        staffTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = new JButton("Add Scheduler");
        addBtn.addActionListener(e -> addScheduler());
        JButton editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(e -> editScheduler());
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteScheduler());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Scheduler> schedulers = userService.getAllSchedulers();
        for (Scheduler s : schedulers) {
            tableModel.addRow(new Object[]{s.getUserId(), s.getFullName(), s.getEmail(), s.getRole(), s.getStatus()});
        }
    }

    private void addScheduler() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Scheduler", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Scheduler newScheduler = new Scheduler(
                    IdGenerator.generateUserId(),
                    name,
                    email,
                    pass,
                    "ACTIVE",
                    IdGenerator.generateUserId(),
                    "SCHEDULER",
                    LocalDate.now()
            );
            userService.addScheduler(newScheduler);
            JOptionPane.showMessageDialog(this, "Scheduler added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }

    private void editScheduler() {
        int row = staffTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String staffId = (String) tableModel.getValueAt(row, 0);
        Scheduler scheduler = null;
        for (Scheduler s : userService.getAllSchedulers()) {
            if (s.getUserId().equals(staffId)) {
                scheduler = s;
                break;
            }
        }

        if (scheduler != null) {
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            JTextField nameField = new JTextField(scheduler.getFullName());
            JTextField emailField = new JTextField(scheduler.getEmail());

            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Scheduler", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Scheduler updated = new Scheduler(
                        scheduler.getUserId(),
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        scheduler.getPassword(),
                        scheduler.getStatus(),
                        scheduler.getStaffId(),
                        scheduler.getRole(),
                        scheduler.getJoinedDate()
                );
                userService.updateScheduler(updated);
                JOptionPane.showMessageDialog(this, "Scheduler updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }

    private void deleteScheduler() {
        int row = staffTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String staffId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this staff member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userService.deleteScheduler(staffId);
            JOptionPane.showMessageDialog(this, "Staff deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }
}
