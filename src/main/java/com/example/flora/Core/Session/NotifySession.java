package com.example.flora.Core.Session;

import com.example.flora.Features.Home.services.NotificationService;

public class NotifySession {
    private static NotificationService notificationService;

    public static void setNotificationService(NotificationService ns) {
        notificationService = ns;
    }

    public static NotificationService getNotificationService() {
        return notificationService;
    }

    public static void clear(){
        notificationService= null;
    }
}
