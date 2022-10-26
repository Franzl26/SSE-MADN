package app;

import dialogs.LoginPane;
import javafx.application.Application;
import javafx.stage.Stage;

public class Client4Start extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginPane.LoginPaneStart();
    }

    public static void main(String[] args) {
        launch();
    }
}
