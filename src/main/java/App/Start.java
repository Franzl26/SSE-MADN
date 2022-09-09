package App;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Locale;

public class Start extends Application {
    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);
        //RootPane root = new RootPane();
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 1300, 400);

        stage.setTitle("Questionnaire");
        stage.setScene(scene);
        stage.show();
        stage.hide();

        GamePane.GamePaneStart();
    }

    public static void main(String[] args) {
        launch();
    }
}
