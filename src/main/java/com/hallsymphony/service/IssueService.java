package com.hallsymphony.service;

import com.hallsymphony.model.issue.Issue;
import com.hallsymphony.model.issue.IssueStatus;
import com.hallsymphony.util.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class IssueService {
    private static final Path ISSUE_FILE = Paths.get("data", "issues.txt");

    public IssueService() {
        ensureDataFiles();
    }

    private void ensureDataFiles() {
        try {
            if (Files.notExists(ISSUE_FILE.getParent())) {
                Files.createDirectories(ISSUE_FILE.getParent());
            }
            if (Files.notExists(ISSUE_FILE)) {
                Files.write(ISSUE_FILE, Collections.singletonList("# Issue data file"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> readLines() throws IOException {
        return FileHandler.readFromFile(ISSUE_FILE);
    }

    private void writeLines(List<String> lines) throws IOException {
        FileHandler.writeToFile(ISSUE_FILE, lines);
    }

    private Optional<Issue> parseIssue(String line) {
        if (line == null || line.trim().isEmpty() || line.startsWith("#")) {
            return Optional.empty();
        }
        String[] parts = line.split("\\|");
        if (parts.length < 5) {
            return Optional.empty();
        }
        String issueId = parts[0].trim();
        String bookingId = parts[1].trim();
        String description = parts[2].trim();
        LocalDate raisedDate = LocalDate.parse(parts[3].trim());
        IssueStatus status = IssueStatus.valueOf(parts[4].trim());
        return Optional.of(new Issue(issueId, bookingId, description, raisedDate, status));
    }

    private String issueToLine(Issue issue) {
        return String.join("|",
                issue.getIssueId(),
                issue.getBookingId(),
                issue.getDescription(),
                issue.getRaisedDate().toString(),
                issue.getIssueStatus().name());
    }

    public void raiseIssue(Issue issue) {
        try {
            List<String> lines = readLines();
            lines.add(issueToLine(issue));
            writeLines(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        try {
            List<String> lines = readLines();
            for (String line : lines) {
                parseIssue(line).ifPresent(issues::add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return issues;
    }

    public void updateIssueStatus(String issueId, IssueStatus status) {
        try {
            List<String> lines = readLines();
            for (int i = 0; i < lines.size(); i++) {
                Optional<Issue> opt = parseIssue(lines.get(i));
                if (opt.isPresent() && opt.get().getIssueId().equals(issueId)) {
                    Issue issue = opt.get();
                    issue.updateStatus(status);
                    lines.set(i, issueToLine(issue));
                    writeLines(lines);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
