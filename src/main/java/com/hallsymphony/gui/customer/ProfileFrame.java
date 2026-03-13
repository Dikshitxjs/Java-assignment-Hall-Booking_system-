package com.hallsymphony.gui.customer;

import com.hallsymphony.model.user.Customer;
import com.hallsymphony.service.UserService;

import javax.swing.*;
import java.awt.*;

public class ProfileFrame extends JFrame {
    private final UserService userService;
    private final Customer customer;

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField addressField;

    public ProfileFrame(Customer customer) {
        super("Update Profile");
        this.customer = customer;
        this.userService = new UserService();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Update Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addField(formPanel, "Full Name:", nameField = new JTextField(), customer.getFullName());
        addField(formPanel, "Phone Number:", phoneField = new JTextField(), customer.getPhoneNumber());
        addField(formPanel, "Address:", addressField = new JTextField(), customer.getAddress());

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        emailLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));
        JLabel emailValueLabel = new JLabel(customer.getEmail());
        emailValueLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        emailValueLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(emailValueLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setBackground(new Color(52, 152, 219));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(130, 40));
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handleSave());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 13));
        cancelBtn.setBackground(new Color(230, 230, 230));
        cancelBtn.setForeground(new Color(80, 80, 80));
        cancelBtn.setPreferredSize(new Dimension(130, 40));
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        formPanel.add(buttonPanel);

        mainPanel.add(formPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, String label, JTextField field, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setForeground(new Color(50, 50, 50));
        panel.add(labelComponent);
        panel.add(Box.createVerticalStrut(5));

        field.setText(value);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer updatedCustomer = new Customer(
                customer.getUserId(),
                name,
                customer.getEmail(),
                customer.getPassword(),
                customer.getStatus(),
                phone,
                address,
                customer.getRegistrationDate()
        );

        userService.updateCustomerProfile(updatedCustomer);
        JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
