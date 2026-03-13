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
        setSize(420, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(this::handleLogin);
        form.add(loginBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        form.add(exitBtn);

        add(new JLabel("Welcome to Hall Symphony", SwingConstants.CENTER), BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
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

    public static void run() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
