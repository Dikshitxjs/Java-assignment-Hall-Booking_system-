package com.hallsymphony.model.payment;

import java.time.LocalDate;

public class Receipt {
    private String receiptId;
    private Payment paymentDetails;
    private LocalDate issuedDate;

    public Receipt(String receiptId, Payment paymentDetails, LocalDate issuedDate) {
        this.receiptId = receiptId;
        this.paymentDetails = paymentDetails;
        this.issuedDate = issuedDate;
    }

    public void displayReceipt() {
        System.out.printf("Receipt %s\nPayment %s: %.2f on %s\n", receiptId,
                paymentDetails.getPaymentId(), paymentDetails.getAmount(), issuedDate);
    }

    public void exportReceipt() {
        // TODO: Export receipt (e.g., to text file)
    }

    // Getters
    public String getReceiptId() { return receiptId; }
    public Payment getPaymentDetails() { return paymentDetails; }
    public LocalDate getIssuedDate() { return issuedDate; }
}
