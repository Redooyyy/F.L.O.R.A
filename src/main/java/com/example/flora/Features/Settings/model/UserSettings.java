package com.example.flora.Features.Settings.model;

public class UserSettings {

    private final String userId;
    private String displayName;
    private String email;
    private String bio;
    private String avatarColor;
    private boolean notifyOnTaskAssign;
    private boolean notifyOnBugReport;
    private boolean notifyOnMention;
    private String theme;

    public UserSettings(String userId, String displayName, String email, String bio, String avatarColor, boolean notifyOnTaskAssign, boolean notifyOnBugReport, boolean notifyOnMention, String theme) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.bio = bio;
        this.avatarColor = avatarColor;
        this.notifyOnTaskAssign = notifyOnTaskAssign;
        this.notifyOnBugReport = notifyOnBugReport;
        this.notifyOnMention = notifyOnMention;
        this.theme = theme;
    }


    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    public boolean isNotifyOnTaskAssign() {
        return notifyOnTaskAssign;
    }

    public boolean isNotifyOnBugReport() {
        return notifyOnBugReport;
    }

    public boolean isNotifyOnMention() {
        return notifyOnMention;
    }

    public String getTheme() {
        return theme;
    }


    public void setDisplayName(String v) {
        this.displayName = v;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public void setBio(String v) {
        this.bio = v;
    }

    public void setAvatarColor(String v) {
        this.avatarColor = v;
    }

    public void setNotifyOnTaskAssign(boolean v) {
        this.notifyOnTaskAssign = v;
    }

    public void setNotifyOnBugReport(boolean v) {
        this.notifyOnBugReport = v;
    }

    public void setNotifyOnMention(boolean v) {
        this.notifyOnMention = v;
    }

    public void setTheme(String v) {
        this.theme = v;
    }
}