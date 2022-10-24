package dialogs;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static dialogs.CommunicationWithServer.tryToLogin;

public class LoginPane extends AnchorPane {
    public LoginPane() {
        TextField serverTextField = new TextField("localhost");
        serverTextField.setPromptText("server");
        serverTextField.setPrefWidth(280);

        TextField usernameTextField = new TextField("Frank");
        usernameTextField.setPromptText("Benutzername");
        usernameTextField.setPrefWidth(280);

        PasswordField passwordField = new PasswordField();
        passwordField.setText("1234");
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(280);

        Button registrierenButton = new Button("Registrieren");
        registrierenButton.addEventHandler(ActionEvent.ACTION, e -> {
            RegisterPane.RegisterPaneStart();
            ((Stage) getScene().getWindow()).close();
        });
        Button abbrechenButton = new Button("Abbrechen");
        abbrechenButton.addEventHandler(ActionEvent.ACTION, e -> System.exit(0));
        Button anmeldenButton = new Button("Anmelden");
        anmeldenButton.addEventHandler(ActionEvent.ACTION, e -> {
            int ret = tryToLogin(serverTextField.getText(), usernameTextField.getText(), passwordField.getText());
            if (ret == 1) {
                ((Stage) getScene().getWindow()).close();
            } else if (ret == -1) {
                new Alert(Alert.AlertType.INFORMATION, "Login-Daten fehlerhaft").showAndWait();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Server nicht gefunden").showAndWait();
            }
        });

        AnchorPane.setLeftAnchor(serverTextField, 10.0);
        AnchorPane.setTopAnchor(serverTextField, 10.0);
        AnchorPane.setLeftAnchor(usernameTextField, 10.0);
        AnchorPane.setTopAnchor(usernameTextField, 40.0);
        AnchorPane.setLeftAnchor(passwordField, 10.0);
        AnchorPane.setTopAnchor(passwordField, 70.0);
        AnchorPane.setLeftAnchor(registrierenButton, 10.0);
        AnchorPane.setBottomAnchor(registrierenButton, 10.0);
        AnchorPane.setRightAnchor(abbrechenButton, 90.0);
        AnchorPane.setBottomAnchor(abbrechenButton, 10.0);
        AnchorPane.setRightAnchor(anmeldenButton, 10.0);
        AnchorPane.setBottomAnchor(anmeldenButton, 10.0);

        getChildren().addAll(abbrechenButton, serverTextField, usernameTextField, passwordField, registrierenButton, anmeldenButton);
    }

    public static void LoginPaneStart() {
        LoginPane root = new LoginPane();
        Scene scene = new Scene(root, 300, 150);
        Stage stage = new Stage();

        stage.setTitle("Anmelden");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
