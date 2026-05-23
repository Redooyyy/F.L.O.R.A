package com.example.flora.Features.Bug.model;

/**
 * Bug model
 * Fields mirror what's used in ProjectDetailUI_Controller's bug section.
 */
public class Bug {

    private final String id;
    private final String projectName;
    private final String title;
    private final String description;
    private final BugSeverity severity;
    private BugStatus status;
    private String fixingUserId;          // null = unclaimed
    private final String reportedByUserId;
    private final String reportedDate;

    public Bug(String id, String projectName, String title, String description,
               BugSeverity severity, BugStatus status,
               String fixingUserId, String reportedByUserId, String reportedDate) {
        this.id               = id;
        this.projectName      = projectName;
        this.title            = title;
        this.description      = description;
        this.severity         = severity;
        this.status           = status;
        this.fixingUserId     = fixingUserId;
        this.reportedByUserId = reportedByUserId;
        this.reportedDate     = reportedDate;
    }

    /* ── Convenience ── */
    public boolean isUnclaimed() { return fixingUserId == null || fixingUserId.isBlank(); }
    public boolean isClosed()    { return status == BugStatus.CLOSED; }

    /* ── Getters ── */
    public String      getId()               { return id; }
    public String      getProjectName()      { return projectName; }
    public String      getTitle()            { return title; }
    public String      getDescription()      { return description; }
    public BugSeverity getSeverity()         { return severity; }
    public BugStatus   getStatus()           { return status; }
    public String      getFixingUserId()     { return fixingUserId; }
    public String      getReportedByUserId() { return reportedByUserId; }
    public String      getReportedDate()     { return reportedDate; }

    /* ── Setters (mutable for status updates) ── */
    public void setStatus(BugStatus status)           { this.status = status; }
    public void setFixingUserId(String fixingUserId)  { this.fixingUserId = fixingUserId; }
}