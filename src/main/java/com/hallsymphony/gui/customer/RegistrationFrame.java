package com.hallsymphony.gui.customer;

import com.hallsymphony.model.user.Customer;
import com.hallsymphony.service.UserService;
import com.hallsymphony.util.IdGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class RegistrationFrame extends JFrame {
    private final UserService userService;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    private JTextField addressField;

    public RegistrationFrame() {
        super("Hall Symphony - Register");
        this.userService = new UserService();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addField(formPanel, "Full Name:", nameField = new JTextField());
        addField(formPanel, "Email:", emailField = new JTextField());
        addField(formPanel, "Password:", passwordField = new JPasswordField());
        addField(formPanel, "Confirm Password:", confirmPasswordField = new JPasswordField());
        addField(formPanel, "Phone Number:", phoneField = new JTextField());
        addField(formPanel, "Address:", addressField = new JTextField());

        formPanel.add(Box.createVerticalStrut(20));

        JButton registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setBackground(new Color(52, 152, 219));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBorder(BorderFactory.createEmptyBorder());
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> handleRegister());
        formPanel.add(registerBtn);

        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        mainPanel.add(formPanel);

        mainPanel.add(Box.createVerticalStrut(15));

        JButton backBtn = new JButton("Back to Login");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        backBtn.setBackground(new Color(230, 230, 230));
        backBtn.setForeground(new Color(80, 80, 80));
        backBtn.setBorder(BorderFactory.createEmptyBorder());
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> goBackToLogin());
        mainPanel.add(backBtn);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, String label, JComponent field) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setForeground(new Color(50, 50, 50));
        panel.add(labelComponent);
        panel.add(Box.createVerticalStrut(5));

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Arial", Font.PLAIN, 13));
        } else if (field instanceof JPasswordField) {
            ((JPasswordField) field).setFont(new Font("Arial", Font.PLAIN, 13));
        }
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        ((JComponent) field).setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userService.isEmailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already registered. Please login or use a different email.", "Email Exists", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String customerId = IdGenerator.generateUserId();
            Customer newCustomer = new Customer(
                    customerId,
                    name,
                    email,
                    password,
                    "ACTIVE",
                    phone,
                    address,
                    LocalDate.now()
            );

            userService.registerCustomer(newCustomer);
            JOptionPane.showMessageDialog(this, "Registration successful! Logging in...", "Success", JOptionPane.INFORMATION_MESSAGE);

            new CustomerDashboard(newCustomer,
                    new com.hallsymphony.service.HallService(),
                    new com.hallsymphony.service.BookingService(),
                    new com.hallsymphony.service.PaymentService(),
                    new com.hallsymphony.service.IssueService()).setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void goBackToLogin() {
        com.hallsymphony.gui.login.LoginFrame.run();
        dispose();
    }

    public static void run() {
        SwingUtilities.invokeLater(() -> {
            RegistrationFrame frame = new RegistrationFrame();
            frame.setVisible(true);
        });
    }
}
