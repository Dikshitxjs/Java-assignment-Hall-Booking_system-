package com.hallsymphony.model.issue;

import java.time.LocalDateTime;

public class MaintenanceTask {
    private String taskId;
    private String issueId;
    private String schedulerId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public MaintenanceTask(String taskId, String issueId, String schedulerId,
                           LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.taskId = taskId;
        this.issueId = issueId;
        this.schedulerId = schedulerId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void assignTask() {
        // TODO: Assign task logic
    }

    public void updateTaskStatus() {
        // TODO: Update status (e.g., in a file)
    }

    public void completeTask() {
        this.endDateTime = LocalDateTime.now();
    }

    // Getters
    public String getTaskId() { return taskId; }
    public String getIssueId() { return issueId; }
    public String getSchedulerId() { return schedulerId; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
}
