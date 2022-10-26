package dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Meldungen {
    public static void kommunikationAbgebrochen() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Kommunikation unterbrochen");
        alert.setContentText("Kommunikation mit Server ist abgebrochen, Spiel wird beendet.");
        alert.setGraphic(null);
        alert.showAndWait();
    }

    public static void zeigeInformation(String ueberschrift, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(ueberschrift);
        alert.setContentText(text);
        alert.setGraphic(null);
        alert.showAndWait();
    }

    public static boolean frageBestaetigung(String ueberschrift, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText(ueberschrift);
        alert.setContentText(text);
        alert.setGraphic(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty()) return false;
        return result.get() == ButtonType.OK;
    }
}
