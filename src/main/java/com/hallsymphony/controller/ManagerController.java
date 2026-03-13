package com.hallsymphony.controller;

import com.hallsymphony.service.IssueService;
import com.hallsymphony.service.SalesService;

public class ManagerController {
    private final SalesService salesService = new SalesService();
    private final IssueService issueService = new IssueService();

    public void viewSales() {
        // TODO: display sales reports
    }

    public void manageIssues() {
        // TODO: manage issues
    }

    public void assignScheduler() {
        // TODO: assign scheduler to issue
    }
}
