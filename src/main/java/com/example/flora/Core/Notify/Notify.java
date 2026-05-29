package com.example.flora.Core.Notify;

import com.example.flora.Core.Session.NotifySession;
import com.example.flora.Features.Home.services.NotificationService;

public final class Notify {

    private Notify() {}

    private static NotificationService svc() {
        return NotifySession.getNotificationService();
    }

    public static void invite(int receiverId, int projectId, String projectName, String senderName, String role) {
        svc().notifyProjectInvite(receiverId, projectId, projectName, senderName, role);
    }

    public static void removedFromProject(int receiverId, String projectName, String senderName) {
        svc().create(receiverId, "Removed from project",
                String.format("You have been removed from the project \"%s\" by %s.", projectName, senderName));
    }

    public static void projectDeleted(int receiverId, String projectName) {
        svc().create(receiverId, "Project deleted",
                String.format("The project \"%s\" has been deleted by its leader.", projectName));
    }

    public static void projectCreated(int receiverId, String projectName) {
        svc().create(receiverId, "Project created",
                String.format("Your project \"%s\" has been created successfully.", projectName));
    }

    public static void taskAssigned(int receiverId, String taskName, String projectName, String senderName) {
        svc().notifyTaskAssigned(receiverId, taskName, projectName, senderName);
    }

    public static void taskUpdated(int receiverId, String taskName, String projectName, String senderName) {
        svc().create(receiverId, "Task updated",
                String.format("\"%s\" in project \"%s\" was updated by %s.", taskName, projectName, senderName));
    }

    public static void taskDueSoon(int receiverId, String taskName, String projectName, int daysLeft) {
        svc().create(receiverId, "Task due soon",
                String.format("Task \"%s\" in project \"%s\" is due in %d day(s). Don't forget to wrap it up!", taskName, projectName, daysLeft));
    }

    public static void taskCompleted(int receiverId, String taskName, String projectName, String completedBy) {
        svc().create(receiverId, "Task completed",
                String.format("\"%s\" in project \"%s\" was marked complete by %s.", taskName, projectName, completedBy));
    }

    public static void passwordChanged(int receiverId) {
        svc().create(receiverId, "Password changed",
                "Your account password was changed successfully. If you did not make this change, please contact support immediately.");
    }

    public static void emailChanged(int receiverId, String newEmail) {
        svc().create(receiverId, "Email address updated",
                String.format("Your account email has been updated to \"%s\". If this wasn't you, please secure your account immediately.", newEmail));
    }

    public static void usernameChanged(int receiverId, String newUsername) {
        svc().create(receiverId, "Username updated",
                String.format("Your display name has been changed to \"%s\".", newUsername));
    }

    public static void profilePictureChanged(int receiverId) {
        svc().create(receiverId, "Profile picture updated",
                "Your profile picture has been updated successfully.");
    }

    public static void twoFactorEnabled(int receiverId) {
        svc().create(receiverId, "Two-factor authentication enabled",
                "Two-factor authentication (2FA) has been enabled on your account. Your account is now more secure.");
    }
    

    //HEHE for myself only
    public static void systemAnnouncement(int receiverId, String title, String message) {
        svc().create(receiverId, title, message);
    }

    public static void newLoginDetected(int receiverId, String location) {
        svc().create(receiverId, "New login detected",
                String.format("A new sign-in to your FLORA account was detected from: %s. If this was you, no action is needed. Otherwise, change your password immediately.", location));
    }

    public static void accountDeactivated(int receiverId) {
        svc().create(receiverId, "Account deactivated",
                "Your FLORA account has been deactivated. Please contact support if you believe this is a mistake.");
    }

    public static void welcome(int receiverId, String username) {
        svc().create(receiverId, "Welcome to FLORA!",
                String.format("Hey %s! 🌸 We're glad to have you. Start by joining or creating a project from your dashboard.", username));
    }
}