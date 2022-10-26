package dialogs;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegisterPane extends AnchorPane {
    public RegisterPane() {
        TextField serverTextField = new TextField("localhost");
        serverTextField.setPromptText("Server-IP-Adresse");
        serverTextField.setPrefWidth(280);

        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Benutzername");
        usernameTextField.setPrefWidth(280);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(280);

        PasswordField passwordField2 = new PasswordField();
        passwordField2.setPromptText("Passwort wiederholen");
        passwordField2.setPrefWidth(280);

        Button registrierenButton = new Button("Registrieren");
        registrierenButton.setPrefWidth(90);
        registrierenButton.addEventHandler(ActionEvent.ACTION, e -> {
            int ret = CommunicationWithServer.tryToRegister(serverTextField.getText(), usernameTextField.getText(), passwordField.getText(), passwordField2.getText());
            if (ret == -1) {
                Meldungen.zeigeInformation("Passwörter stimmen nicht überein","Die beiden eingegebenen Passwörter stimmen nicht überein.");
            } else if (ret == -2) {
                Meldungen.zeigeInformation("Passwort entspricht nicht den Richtlinien", "Das Passwort entspricht nicht den Richtlinien:\n- 8-15 Zeichen\n- mindestens ein Buchstabe\n- mindestens eine Zahl\n- mindestens eins der Sonderzeichen: !§$%&/()=?#");
            } else if (ret == -3) {
                Meldungen.zeigeInformation("Benutzername entspricht nicht den Richtlinien", "Der Benutzername entspricht nicht den Richtlinien:\n- 3-8 Zeichen\n- nur Buchstaben");
            } else if (ret == -4) {
                Meldungen.zeigeInformation("Server nicht gefunden", "Unter der angegebenen IP-Adresse konnte kein Server gefunden werden.");
            } else if (ret == -5) {
                Meldungen.zeigeInformation("Benutzername bereits vergeben", "Dieser Benutzername ist bereits vergeben, versuche es mit einem anderen nochmal");
            } else {
                Meldungen.zeigeInformation("Registrierung erfolgreich!", "Die Registrierung war erfolgreich, du kannst dich jetzt anmelden");
                ((Stage) getScene().getWindow()).close();
            }
        });
        Button abbrechenButton = new Button("Abbrechen");
        abbrechenButton.addEventHandler(ActionEvent.ACTION, e -> {
            LoginPane.LoginPaneStart();
            ((Stage) getScene().getWindow()).close();
        });

        AnchorPane.setLeftAnchor(serverTextField, 10.0);
        AnchorPane.setTopAnchor(serverTextField, 10.0);
        AnchorPane.setLeftAnchor(usernameTextField, 10.0);
        AnchorPane.setTopAnchor(usernameTextField, 40.0);
        AnchorPane.setLeftAnchor(passwordField, 10.0);
        AnchorPane.setTopAnchor(passwordField, 70.0);
        AnchorPane.setLeftAnchor(passwordField2, 10.0);
        AnchorPane.setTopAnchor(passwordField2, 100.0);
        AnchorPane.setRightAnchor(registrierenButton, 10.0);
        AnchorPane.setBottomAnchor(registrierenButton, 10.0);
        AnchorPane.setRightAnchor(abbrechenButton, 110.0);
        AnchorPane.setBottomAnchor(abbrechenButton, 10.0);

        getChildren().addAll(abbrechenButton, serverTextField, usernameTextField, passwordField, passwordField2, registrierenButton);
    }

    private void setOnClose() {
        getScene().getWindow().setOnCloseRequest(e -> {
            LoginPane.LoginPaneStart();
            ((Stage) getScene().getWindow()).close();
        });
    }

    public static void RegisterPaneStart() {
        RegisterPane root = new RegisterPane();
        Scene scene = new Scene(root, 300, 180);
        Stage stage = new Stage();

        stage.setTitle("Registrieren");
        stage.setScene(scene);
        stage.setResizable(false);
        root.setOnClose();
        stage.show();
    }
}
