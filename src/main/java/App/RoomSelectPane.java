package App;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class RoomSelectPane extends AnchorPane {
    private ListView<HBox> roomsList;

    public RoomSelectPane(String name) {
        Canvas nameCanvas = new Canvas(300,40);
        GraphicsContext gcName = nameCanvas.getGraphicsContext2D();
        gcName.setFont(Font.font(30));
        gcName.fillText(name, 5, 30, 300);

        roomsList = new ListView<>();

        Button newGameButton = new Button("Neues Spiel erstellen");
        newGameButton.addEventHandler(ActionEvent.ACTION, e -> {

        });
        Button exitButton = new Button("Beenden");
        exitButton.addEventHandler(ActionEvent.ACTION, e -> {

        });


    }

    public void displayRooms(Rooms roomsTogether) {
        ArrayList<Room> rooms = roomsTogether.getRooms();
        for (Room r : rooms) {
            HBox box = new HBox();
            Canvas canvas = new Canvas(800, 20);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Button button = new Button("Beitreten");
            button.addEventHandler(ActionEvent.ACTION, e -> {

            });

            Players players = r.getPlayers();
            StringBuilder build = new StringBuilder();
            build.append(players.getCount()).append("/4    ");



            roomsList.getItems().add(box);
        }
    }

    public static void RoomSelectPaneStart(String name) {
        RoomSelectPane root = new RoomSelectPane(name);
        Scene scene = new Scene(root, 1000, 500);
        Stage stage = new Stage();

        stage.setTitle("Registrieren");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
