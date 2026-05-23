package com.example.flora.Features.Bug.model;

public enum BugSeverity {
    CRITICAL, HIGH, MEDIUM, LOW;

    public String displayName() {
        return switch (this) {
            case CRITICAL -> "🔴 CRITICAL";
            case HIGH     -> "🟠 HIGH";
            case MEDIUM   -> "🟡 MEDIUM";
            case LOW      -> "🟢 LOW";
        };
    }
}