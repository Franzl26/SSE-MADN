package Dialogs;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginPane extends AnchorPane {
    public LoginPane() {
        TextField serverTextField = new TextField("localhost");
        serverTextField.setPromptText("Server");
        serverTextField.setPrefWidth(280);

        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Benutzername");
        usernameTextField.setPrefWidth(280);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(280);

        Button registrierenButton = new Button("Registrieren");
        registrierenButton.addEventHandler(ActionEvent.ACTION, e -> {

        });
        Button abbrechenButton = new Button("Abbrechen");
        abbrechenButton.addEventHandler(ActionEvent.ACTION, e -> {

        });
        Button anmeldenButton = new Button("Anmelden");
        anmeldenButton.addEventHandler(ActionEvent.ACTION, e -> {

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

        getChildren().addAll(abbrechenButton, serverTextField, usernameTextField, passwordField,
                registrierenButton, anmeldenButton);
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
