package Dialogs;

import ClientLogic.CommunicationWithServer;
import DataAndMethods.Room;
import DataAndMethods.Rooms;
import RMIInterfaces.UpdateRoomsInterface;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

public class RoomSelectPane extends AnchorPane {
    private final ListView<HBox> roomsList;
    private final UpdateRoomsInterface uri;

    public RoomSelectPane(String username) {
        try {
            uri = new UpdateRoomsObject(this);
        } catch (RemoteException e) {
            throw new RuntimeException("UpdateRoomsObject konnte nicht erstellt werden");
        }

        Canvas nameCanvas = new Canvas(300, 40);
        GraphicsContext gcName = nameCanvas.getGraphicsContext2D();
        gcName.setFont(Font.font(30));
        gcName.fillText(username, 5, 30, 300);

        roomsList = new ListView<>();
        roomsList.setPrefWidth(800);

        Button newGameButton = new Button("Neues Spiel erstellen");
        newGameButton.addEventHandler(ActionEvent.ACTION, e -> {
            int ret = CommunicationWithServer.createNewRoom(uri);
            if (ret == -1) {
                new Alert(Alert.AlertType.INFORMATION, "Maximale Raumanzahl bereits erreicht").showAndWait();
            } else {
                CommunicationWithServer.unsubscribeUpdateRooms(uri);
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
        Vector<Room> rooms = roomsTogether.getRooms();
        for (Room r : rooms) {
            HBox box = new HBox();
            Canvas canvas = new Canvas(700, 30);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Button button = new Button("Beitreten");
            button.addEventHandler(ActionEvent.ACTION, e -> {
                int ret = CommunicationWithServer.enterRoom(uri, r);
                if (ret == -1) {
                    new Alert(Alert.AlertType.INFORMATION, "Raum bereits voll").showAndWait();
                } else {
                    CommunicationWithServer.unsubscribeUpdateRooms(uri);
                    ((Stage) getScene().getWindow()).close();
                }
            });

            ArrayList<String> players = r.getPlayers();
            StringBuilder build = new StringBuilder();
            build.append(players.size()).append("/4    ");
            for (String p : players) {
                build.append(p).append("  ");
            }
            gc.setLineWidth(1.0);
            gc.setFont(Font.font(20));
            gc.fillText(build.toString(), 5, 20, 700);
            button.setAlignment(Pos.CENTER_RIGHT);
            box.getChildren().addAll(canvas, button);

            roomsList.getItems().add(box);
        }
    }

    public UpdateRoomsInterface getURI() {
        return uri;
    }

    private void setOnClose() {
        getScene().getWindow().setOnCloseRequest(e -> {
            beenden();
            e.consume();
        });

    }

    private void beenden() {
        new Alert(Alert.AlertType.CONFIRMATION, "Willst du das Spiel wirklich beenden?").showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                CommunicationWithServer.unsubscribeUpdateRooms(uri);
                CommunicationWithServer.logout(uri);
                System.exit(0);
            }
        });
    }

    public static RoomSelectPane RoomSelectPaneStart(String username) {
        RoomSelectPane root = new RoomSelectPane(username);
        Scene scene = new Scene(root, 820, 500);
        Stage stage = new Stage();

        stage.setTitle("Raum Auswahl");
        stage.setScene(scene);
        stage.setResizable(false);
        root.setOnClose();
        //stage.show();
        return root;
    }
}
