package monopoly.client;

import javafx.scene.control.Alert;

public class Utils {
    public static void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
