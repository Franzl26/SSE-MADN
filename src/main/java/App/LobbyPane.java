package App;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LobbyPane extends AnchorPane {
    public LobbyPane() {
        //todo
    }

    public static void LobbyPaneStart() {
        LobbyPane root = new LobbyPane();
        Scene scene = new Scene(root, 300, 150);
        Stage stage = new Stage();

        stage.setTitle("Registrieren");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
