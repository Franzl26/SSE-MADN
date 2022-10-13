package App;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

public class VideoTestPane extends AnchorPane {
    public VideoTestPane() {
        File f = new File("C:/Users/f-luc/Downloads/WhatsApp Video 2022-10-13 at 17.29.22.mp4");
        Media media = new Media(f.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);
        view.setFitHeight(500);
        view.setFitWidth(500);
        Group group = new Group(view);

        getChildren().addAll(group);
        player.play();


        // todo
    }

    public static void VideoTestPaneStart() {
        VideoTestPane root = new VideoTestPane();
        Scene scene = new Scene(root, 500, 500);
        Stage stage = new Stage();

        stage.setTitle("Design Auswählen");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
