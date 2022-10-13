package Dialogs;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DesignPane extends AnchorPane {
    public DesignPane() {



        // todo
    }

    public static void DesignPaneStart() {
        DesignPane root = new DesignPane();
        Scene scene = new Scene(root, 400, 200);
        Stage stage = new Stage();

        stage.setTitle("Design Auswählen");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
