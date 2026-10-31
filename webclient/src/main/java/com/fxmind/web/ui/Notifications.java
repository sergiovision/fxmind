package com.fxmind.web.ui;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Consistent success/error toasts across views. */
public final class Notifications {

    private static final int DURATION_MS = 4_000;

    private Notifications() {}

    public static void success(String message) {
        Notification.show(message, DURATION_MS, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public static void error(String message) {
        Notification.show(message, DURATION_MS, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
