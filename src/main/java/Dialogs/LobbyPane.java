package Dialogs;

import DataAndMethods.Players;
import DataAndMethods.Room;
import RMIInterfaces.UpdateLobbyInterface;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class LobbyPane extends AnchorPane {
    private final GraphicsContext gcName;
    private final UpdateLobbyInterface uli;

    public LobbyPane() {
        try {
            uli = new UpdateLobbyObject(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        Canvas nameCanvas = new Canvas(200, 150);
        gcName = nameCanvas.getGraphicsContext2D();

        Button botAddButton = new Button("Bot hinzufügen");
        botAddButton.setPrefWidth(100);
        botAddButton.addEventHandler(ActionEvent.ACTION, e -> {

        });
        Button botRemoveButton = new Button("Bot entfernen");
        botRemoveButton.setPrefWidth(100);
        botRemoveButton.addEventHandler(ActionEvent.ACTION, e -> {

        });
        Button designButton = new Button("Spieldesign auswählen");
        designButton.setPrefWidth(140);
        designButton.addEventHandler(ActionEvent.ACTION, e -> {

        });
        Button startButton = new Button("Spiel starten");
        startButton.setPrefWidth(140);
        startButton.addEventHandler(ActionEvent.ACTION, e -> {

        });
        Button exitButton = new Button("Warteraum verlassen");
        exitButton.setPrefWidth(140);
        exitButton.addEventHandler(ActionEvent.ACTION, e -> {

        });

        AnchorPane.setLeftAnchor(nameCanvas, 10.0);
        AnchorPane.setTopAnchor(nameCanvas, 10.0);
        AnchorPane.setLeftAnchor(botAddButton, 10.0);
        AnchorPane.setBottomAnchor(botAddButton, 10.0);
        AnchorPane.setLeftAnchor(botRemoveButton, 120.0);
        AnchorPane.setBottomAnchor(botRemoveButton, 10.0);
        AnchorPane.setRightAnchor(exitButton, 10.0);
        AnchorPane.setBottomAnchor(exitButton, 10.0);
        AnchorPane.setRightAnchor(startButton, 10.0);
        AnchorPane.setBottomAnchor(startButton, 40.0);
        AnchorPane.setRightAnchor(designButton, 10.0);
        AnchorPane.setBottomAnchor(designButton, 100.0);

        getChildren().addAll(nameCanvas, botAddButton, botRemoveButton, designButton, startButton, exitButton);



        //testNamesInit();
    }

    public UpdateLobbyInterface getULI() {
        return uli;
    }

    public void drawNames(Room room) {
        Players players = room.getPlayers();
        gcName.setLineWidth(1.0);
        gcName.setFont(Font.font(20));
        gcName.setFill(Color.BLACK);
        for (int i = 0; i < players.getCount(); i++) {
            gcName.fillText(players.getPlayer(i), 5, i*30+20,190);
        }
    }

    public static LobbyPane LobbyPaneStart() {
        LobbyPane root = new LobbyPane();
        Scene scene = new Scene(root, 400, 200);
        Stage stage = new Stage();

        stage.setTitle("Warteraum");
        stage.setScene(scene);
        stage.setResizable(false);
        //stage.show();
        return root;
    }

    private void testNamesInit() {
        Room room = new Room();
        room.addPlayer("Player1");
        room.addPlayer("Player2");
        room.addPlayer("Player3");
        room.addPlayer("Player4");
        drawNames(room);
    }
}
