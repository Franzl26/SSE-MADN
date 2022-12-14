package dialogs;

import dataAndMethods.Room;
import dataAndMethods.Rooms;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Collection;

public class RoomSelectPane extends AnchorPane {
    private final ListView<HBox> roomsList;

    public RoomSelectPane(String username) {

        Canvas nameCanvas = new Canvas(300, 40);
        GraphicsContext gcName = nameCanvas.getGraphicsContext2D();
        gcName.setFont(Font.font(30));
        gcName.fillText(username, 5, 30, 300);

        roomsList = new ListView<>();
        roomsList.setPrefWidth(800);

        Button newGameButton = new Button("Neuen Warteraum erstellen");
        newGameButton.addEventHandler(ActionEvent.ACTION, e -> {
            int ret = CommunicationWithServer.createNewRoom();
            if (ret == -1) {
                Meldungen.zeigeInformation("Maximale Raumanzahl bereits erreicht", "Die maximale Anzahl an Warteräumen ist erreicht, es kann kein neuer erstellt werden.");
            } else {
                CommunicationWithServer.unsubscribeUpdateRooms();
                ((Stage) getScene().getWindow()).close();
            }
        });
        Button exitButton = new Button("Beenden");
        exitButton.addEventHandler(ActionEvent.ACTION, e -> beenden());

        AnchorPane.setLeftAnchor(nameCanvas, 10.0);
        AnchorPane.setTopAnchor(nameCanvas, 10.0);
        AnchorPane.setLeftAnchor(roomsList, 10.0);
        AnchorPane.setTopAnchor(roomsList, 60.0);
        AnchorPane.setLeftAnchor(newGameButton, 10.0);
        AnchorPane.setBottomAnchor(newGameButton, 10.0);
        AnchorPane.setRightAnchor(exitButton, 10.0);
        AnchorPane.setBottomAnchor(exitButton, 10.0);

        getChildren().addAll(nameCanvas, roomsList, newGameButton, exitButton);

    }

    public void displayRooms(Rooms roomsTogether) {
        roomsList.getItems().clear();
        Collection<Room> rooms = roomsTogether.getRooms();
        for (Room r : rooms) {
            HBox box = new HBox();
            Canvas canvas = new Canvas(700, 30);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Button button = new Button("Beitreten");
            button.addEventHandler(ActionEvent.ACTION, e -> {
                int ret = CommunicationWithServer.enterRoom(r.getId());
                if (ret == -1) {
                    Meldungen.zeigeInformation("Warteraum ist voll", "Der Warteraum ist bereits voll, du kannst diesem leider nicht beitreten");
                } else {
                    CommunicationWithServer.unsubscribeUpdateRooms();
                    ((Stage) getScene().getWindow()).close();
                }
            });

            String[] players = r.getPlayers();
            StringBuilder build = new StringBuilder();
            build.append(r.getCount()).append("/4    ");
            for (int i = 0; i < r.getCount(); i++) {
                build.append(players[i]).append("  ");
            }
            gc.setLineWidth(1.0);
            gc.setFont(Font.font(20));
            gc.fillText(build.toString(), 5, 20, 700);
            button.setAlignment(Pos.CENTER_RIGHT);
            box.getChildren().addAll(canvas, button);

            roomsList.getItems().add(box);
        }
    }

    private void setOnClose() {
        getScene().getWindow().setOnCloseRequest(e -> {
            beenden();
            e.consume();
        });

    }

    private void beenden() {
        if (Meldungen.frageBestaetigung("Spiel beenden","Willst du das Spiel wirklich beenden?")) {
            CommunicationWithServer.unsubscribeUpdateRooms();
            CommunicationWithServer.logout();
            System.exit(0);
        }
    }

    public static RoomSelectPane RoomSelectPaneStart(String username) {
        RoomSelectPane root = new RoomSelectPane(username);
        Scene scene = new Scene(root, 820, 500);
        Stage stage = new Stage();

        stage.setTitle("Raumauswahl");
        stage.setScene(scene);
        stage.setResizable(false);
        root.setOnClose();
        //stage.show();
        return root;
    }
}
