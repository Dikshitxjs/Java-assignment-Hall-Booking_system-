package com.hallsymphony.model.issue;

import java.time.LocalDate;

public class Issue {
    private String issueId;
    private String bookingId;
    private String description;
    private LocalDate raisedDate;
    private IssueStatus issueStatus;

    public Issue(String issueId, String bookingId, String description, LocalDate raisedDate, IssueStatus issueStatus) {
        this.issueId = issueId;
        this.bookingId = bookingId;
        this.description = description;
        this.raisedDate = raisedDate;
        this.issueStatus = issueStatus;
    }

    public void raise() {
        this.issueStatus = IssueStatus.IN_PROGRESS;
    }

    public void updateStatus(IssueStatus status) {
        this.issueStatus = status;
    }

    public void closeIssue() {
        this.issueStatus = IssueStatus.CLOSED;
    }

    // Getters
    public String getIssueId() { return issueId; }
    public String getBookingId() { return bookingId; }
    public String getDescription() { return description; }
    public LocalDate getRaisedDate() { return raisedDate; }
    public IssueStatus getIssueStatus() { return issueStatus; }
}
