package ClientLogic;

import Dialogs.LoginPane;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientStart extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginPane.LoginPaneStart();
    }

    public static void main(String[] args) {
        launch();
    }
}
