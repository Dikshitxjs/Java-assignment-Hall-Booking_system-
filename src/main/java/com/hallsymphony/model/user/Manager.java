package com.hallsymphony.model.user;

public class Manager extends Staff {

    public Manager(String userId, String fullName, String email, String password, String status,
                   String staffId, String role, java.time.LocalDate joinedDate) {
        super(userId, fullName, email, password, status, staffId, role, joinedDate);
    }

    public void viewSalesReport() {
        // TODO: Generate sales report (via SalesService)
    }

    public void filterSalesByPeriod() {
        // TODO: Filter sales by period (via SalesService)
    }

    public void viewIssues() {
        // TODO: View issues (via IssueService)
    }

    public void assignScheduler() {
        // TODO: Assign scheduler to issue or task
    }

    public void updateIssueStatus() {
        // TODO: Update issue status (via IssueService)
    }
}
