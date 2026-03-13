package com.hallsymphony.gui.login;

import com.hallsymphony.gui.admin.AdminDashboard;
import com.hallsymphony.gui.customer.CustomerDashboard;
import com.hallsymphony.gui.manager.ManagerDashboard;
import com.hallsymphony.gui.scheduler.SchedulerDashboard;
import com.hallsymphony.model.user.Administrator;
import com.hallsymphony.model.user.Customer;
import com.hallsymphony.model.user.Manager;
import com.hallsymphony.model.user.Scheduler;
import com.hallsymphony.model.user.User;
import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.HallService;
import com.hallsymphony.service.IssueService;
import com.hallsymphony.service.PaymentService;
import com.hallsymphony.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private final UserService userService;
    private final HallService hallService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final IssueService issueService;

    private final JTextField emailField;
    private final JPasswordField passwordField;

    public LoginFrame() {
        super("Hall Symphony - Login");
        this.userService = new UserService();
        this.hallService = new HallService();
        this.bookingService = new BookingService();
        this.paymentService = new PaymentService();
        this.issueService = new IssueService();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("Hall Symphony");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        // Form panel with white background
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        emailLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(8));
        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 13));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(20));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));

        // Login Button
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(52, 152, 219));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(this::handleLogin);
        formPanel.add(loginBtn);

        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        mainPanel.add(formPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        // New User Registration Link
        JButton registerLinkBtn = new JButton("New User? Register here");
        registerLinkBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        registerLinkBtn.setBackground(new Color(230, 230, 230));
        registerLinkBtn.setForeground(new Color(52, 152, 219));
        registerLinkBtn.setBorder(BorderFactory.createEmptyBorder());
        registerLinkBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        registerLinkBtn.setFocusPainted(false);
        registerLinkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLinkBtn.addActionListener(e -> openRegistration());
        mainPanel.add(registerLinkBtn);

        mainPanel.add(Box.createVerticalStrut(10));

        // Exit Button
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        exitBtn.setBackground(new Color(230, 230, 230));
        exitBtn.setForeground(new Color(80, 80, 80));
        exitBtn.setBorder(BorderFactory.createEmptyBorder());
        exitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        exitBtn.setFocusPainted(false);
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitBtn.addActionListener(e -> System.exit(0));
        mainPanel.add(exitBtn);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Missing credentials", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userService.authenticateUser(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials or account blocked.", "Login failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> openDashboard(user));
        dispose();
    }

    private void openDashboard(User user) {
        if (user instanceof Administrator) {
            new AdminDashboard((Administrator) user, userService).setVisible(true);
        } else if (user instanceof Scheduler) {
            new SchedulerDashboard((Scheduler) user, hallService).setVisible(true);
        } else if (user instanceof Manager) {
            new ManagerDashboard((Manager) user, bookingService, paymentService, issueService).setVisible(true);
        } else if (user instanceof Customer) {
            new CustomerDashboard((Customer) user, hallService, bookingService, paymentService, issueService).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "User role not supported.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void openRegistration() {
        com.hallsymphony.gui.customer.RegistrationFrame.run();
        dispose();
    }

    public static void run() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
