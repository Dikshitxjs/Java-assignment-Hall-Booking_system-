package com.hallsymphony.gui.customer;

import com.hallsymphony.model.booking.Booking;
import com.hallsymphony.model.hall.Hall;
import com.hallsymphony.model.payment.Payment;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ReceiptFrame extends JFrame {
    private final Payment payment;
    private final Booking booking;
    private final Hall hall;

    public ReceiptFrame(Payment payment, Booking booking, Hall hall) {
        super("Booking Receipt");
        this.payment = payment;
        this.booking = booking;
        this.hall = hall;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel receiptPanel = new JPanel();
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
        receiptPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Receipt");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(titleLabel);

        receiptPanel.add(Box.createVerticalStrut(10));

        JLabel companyLabel = new JLabel("Hall Symphony Inc.");
        companyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        companyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(companyLabel);

        receiptPanel.add(Box.createVerticalStrut(20));

        addReceiptLine(receiptPanel, "Receipt ID:", payment.getPaymentId());
        addReceiptLine(receiptPanel, "Booking ID:", booking.getBookingId());
        addReceiptLine(receiptPanel, "Payment Date:", payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        receiptPanel.add(Box.createVerticalStrut(20));

        JLabel bookingDetailsTitle = new JLabel("Booking Details:");
        bookingDetailsTitle.setFont(new Font("Arial", Font.BOLD, 13));
        receiptPanel.add(bookingDetailsTitle);

        receiptPanel.add(Box.createVerticalStrut(8));

        addReceiptLine(receiptPanel, "Hall Name:", hall != null ? hall.getHallName() : "Unknown");
        addReceiptLine(receiptPanel, "Booking Date:", booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        addReceiptLine(receiptPanel, "Start Time:", booking.getStartTime().toString());
        addReceiptLine(receiptPanel, "End Time:", booking.getEndTime().toString());

        receiptPanel.add(Box.createVerticalStrut(20));

        JLabel amountLabel = new JLabel("Amount Paid: RM " + String.format("%.2f", payment.getAmount()));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setForeground(new Color(46, 204, 113));
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(amountLabel);

        receiptPanel.add(Box.createVerticalStrut(10));

        JLabel statusLabel = new JLabel("Status: " + payment.getPaymentStatus());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(new Color(52, 152, 219));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(statusLabel);

        receiptPanel.add(Box.createVerticalStrut(30));

        JLabel thanksLabel = new JLabel("Thank you for booking with Hall Symphony!");
        thanksLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        thanksLabel.setForeground(new Color(100, 100, 100));
        thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptPanel.add(thanksLabel);

        mainPanel.add(receiptPanel);

        mainPanel.add(Box.createVerticalStrut(15));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setBackground(new Color(52, 152, 219));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.setMaximumSize(new Dimension(120, 40));
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        mainPanel.add(closeBtn);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addReceiptLine(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setPreferredSize(new Dimension(120, 22));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));

        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
    }
}
