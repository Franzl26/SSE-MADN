package App;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegisterPane extends AnchorPane {
    public RegisterPane() {
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

        });
        Button abbrechenButton = new Button("Abbrechen");
        abbrechenButton.addEventHandler(ActionEvent.ACTION, e -> {

        });

        AnchorPane.setLeftAnchor(usernameTextField, 10.0);
        AnchorPane.setTopAnchor(usernameTextField, 10.0);
        AnchorPane.setLeftAnchor(passwordField, 10.0);
        AnchorPane.setTopAnchor(passwordField, 40.0);
        AnchorPane.setLeftAnchor(passwordField2, 10.0);
        AnchorPane.setTopAnchor(passwordField2, 70.0);
        AnchorPane.setRightAnchor(registrierenButton, 10.0);
        AnchorPane.setBottomAnchor(registrierenButton, 10.0);
        AnchorPane.setRightAnchor(abbrechenButton, 110.0);
        AnchorPane.setBottomAnchor(abbrechenButton, 10.0);

        getChildren().addAll(abbrechenButton, usernameTextField, passwordField, passwordField2,
                registrierenButton);
    }

    public static void RegisterPaneStart() {
        RegisterPane root = new RegisterPane();
        Scene scene = new Scene(root, 300, 150);
        Stage stage = new Stage();

        stage.setTitle("Registrieren");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
