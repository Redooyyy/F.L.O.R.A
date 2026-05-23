package com.example.flora.Features.Bug.model;


public enum BugStatus {
    OPEN,
    IN_PROGRESS,
    CLOSED;
    public String displayName() {
        return switch (this) {
            case OPEN        -> "● Open";
            case IN_PROGRESS -> "⟳ In Progress";
            case CLOSED      -> "✓ Closed";
        };
    }
}