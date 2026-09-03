package com.fitsync.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Thin wrapper around JavaFX {@link Alert} dialogs so every screen shows
 * consistent, header-less pop-ups for errors, confirmations and success
 * messages.
 */
public final class AlertUtil {

    private AlertUtil() {
    }

    public static void showError(String title, String message) {
        show(Alert.AlertType.ERROR, title, message);
    }

    public static void showSuccess(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Shows a modal OK / Cancel dialog.
     *
     * @return {@code true} only if the user pressed OK.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
