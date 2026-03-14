package com.hallsymphony.gui.manager;

import com.hallsymphony.model.issue.Issue;
import com.hallsymphony.model.issue.IssueStatus;
import com.hallsymphony.model.payment.Payment;
import com.hallsymphony.model.user.Manager;
import com.hallsymphony.service.BookingService;
import com.hallsymphony.service.IssueService;
import com.hallsymphony.service.PaymentService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManagerDashboard extends JFrame {
    private final Manager manager;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final IssueService issueService;

    private final DefaultTableModel issueModel;
    private final DefaultTableModel paymentModel;

    public ManagerDashboard(Manager manager, BookingService bookingService, PaymentService paymentService, IssueService issueService) {
        super("Manager Dashboard - " + manager.getFullName());
        this.manager = manager;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.issueService = issueService;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Sales tab
        JPanel salesPanel = new JPanel(new BorderLayout(10, 10));
        salesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel periodLabel = new JLabel("Filter by Period:");
        JComboBox<String> periodCombo = new JComboBox<>(new String[]{"This Week", "This Month", "This Year", "All Time"});
        filterPanel.add(periodLabel);
        filterPanel.add(periodCombo);

        JLabel salesLabel = new JLabel("Total Sales: RM 0.00", SwingConstants.CENTER);
        salesLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton refreshSales = new JButton("Refresh");
        refreshSales.addActionListener(e -> updateSalesLabel(salesLabel, periodCombo.getSelectedItem().toString()));

        periodCombo.addActionListener(e -> updateSalesLabel(salesLabel, periodCombo.getSelectedItem().toString()));

        salesPanel.add(filterPanel, BorderLayout.NORTH);
        salesPanel.add(salesLabel, BorderLayout.CENTER);
        salesPanel.add(refreshSales, BorderLayout.SOUTH);
        tabs.addTab("Sales", salesPanel);

        // Payments tab
        paymentModel = new DefaultTableModel(new String[]{"Payment ID", "Booking ID", "Amount", "Date", "Status"}, 0);
        JTable paymentTable = new JTable(paymentModel);
        JScrollPane paymentScroll = new JScrollPane(paymentTable);
        JButton refreshPayments = new JButton("Refresh");
        refreshPayments.addActionListener(e -> refreshPayments());

        JPanel paymentsPanel = new JPanel(new BorderLayout());
        JPanel paymentButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        paymentButtons.add(refreshPayments);
        paymentsPanel.add(paymentScroll, BorderLayout.CENTER);
        paymentsPanel.add(paymentButtons, BorderLayout.SOUTH);
        tabs.addTab("Payments", paymentsPanel);

        // Issues tab
        issueModel = new DefaultTableModel(new String[]{"Issue ID", "Booking ID", "Description", "Status", "Assigned To"}, 0);
        JTable issueTable = new JTable(issueModel);
        JScrollPane issueScroll = new JScrollPane(issueTable);
        JButton refreshIssues = new JButton("Refresh");
        refreshIssues.addActionListener(e -> refreshIssues());
        JButton changeStatus = new JButton("Change Status");
        changeStatus.addActionListener(e -> changeSelectedIssueStatus(issueTable));

        JPanel issuesPanel = new JPanel(new BorderLayout());
        JPanel issueButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        issueButtons.add(refreshIssues);
        issueButtons.add(changeStatus);
        issuesPanel.add(issueScroll, BorderLayout.CENTER);
        issuesPanel.add(issueButtons, BorderLayout.SOUTH);
        tabs.addTab("Issues", issuesPanel);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new com.hallsymphony.gui.login.LoginFrame().run();
        });

        add(tabs, BorderLayout.CENTER);
        add(logout, BorderLayout.SOUTH);

        refreshSalesAmount(salesLabel);
        refreshPayments();
        refreshIssues();
    }

    private void refreshSalesAmount(JLabel label) {
        List<Payment> payments = paymentService.getAllPayments();
        double total = payments.stream().mapToDouble(Payment::getAmount).sum();
        label.setText(String.format("Total Sales: RM %.2f", total));
    }

    private void updateSalesLabel(JLabel label, String period) {
        double total = 0;
        if (period.equals("This Week")) {
            total = paymentService.getTotalSalesThisWeek();
        } else if (period.equals("This Month")) {
            total = paymentService.getTotalSalesThisMonth();
        } else if (period.equals("This Year")) {
            total = paymentService.getTotalSalesThisYear();
        } else {
            total = paymentService.getTotalSalesAllTime();
        }
        label.setText(String.format("Total Sales (%s): RM %.2f", period, total));
    }

    private void refreshPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        paymentModel.setRowCount(0);
        for (Payment p : payments) {
            paymentModel.addRow(new Object[]{p.getPaymentId(), p.getBookingId(), String.format("%.2f", p.getAmount()), p.getPaymentDate(), p.getPaymentStatus()});
        }
    }

    private void refreshIssues() {
        List<Issue> issues = issueService.getAllIssues();
        issueModel.setRowCount(0);
        for (Issue i : issues) {
            issueModel.addRow(new Object[]{i.getIssueId(), i.getBookingId(), i.getDescription(), i.getIssueStatus(), "Unassigned"});
        }
    }

    private void changeSelectedIssueStatus(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an issue first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String issueId = (String) issueModel.getValueAt(row, 0);
        String status = JOptionPane.showInputDialog(this, "New status (IN_PROGRESS/DONE/CLOSED/CANCELLED):", "Update Status", JOptionPane.PLAIN_MESSAGE);
        if (status == null || status.trim().isEmpty()) {
            return;
        }
        try {
            issueService.updateIssueStatus(issueId, IssueStatus.valueOf(status.trim().toUpperCase()));
            refreshIssues();
            JOptionPane.showMessageDialog(this, "Issue status updated.", "Done", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid status value.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
