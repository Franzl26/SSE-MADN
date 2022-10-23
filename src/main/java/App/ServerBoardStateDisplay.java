package App;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import DataAndMethods.FieldState;
import DataAndMethods.GameStatistics;
import RMIInterfaces.LoggedInInterface;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Arrays;

import static DataAndMethods.BoardDrawer.drawBoardAll;

public class ServerBoardStateDisplay extends AnchorPane { // todo entfernen
    private final GraphicsContext gcBoard;
    private final BoardConfiguration config = BoardConfiguration.loadBoardKonfiguration("./resources/Server/designs/Standard/");
    private final TextArea text;

    public ServerBoardStateDisplay() {
        Canvas canvas = new Canvas(500,500);
        gcBoard = canvas.getGraphicsContext2D();
        text = new TextArea();
        text.setPrefWidth(500);
        text.setPrefHeight(500);

        AnchorPane.setLeftAnchor(text,500.0);

        getChildren().addAll(canvas,text);
    }

    public static String getText(GameStatistics gameStatistics, LoggedInInterface[] clients, String[] names,
                                 FieldState[] fields, boolean[] finished, int anzahlFinished, int spielerAnzahl,
                                 int aktiverSpieler, int zahlGewuerfelt, int anzahlWuerfeln) {
        return "names: " + Arrays.toString(names) + "\nclients: " + Arrays.toString(clients) +
                "\nfields: " + Arrays.toString(fields) + "\nfinished: " + anzahlFinished +
                ": " + Arrays.toString(finished) + "\nspielerAnzahl: " + spielerAnzahl +
                "\naktiverSpieler: " + aktiverSpieler + "  wuerfel: " + zahlGewuerfelt +
                "  anzWuerfel: " + anzahlWuerfeln + "\n" + gameStatistics;
    }

    public void drawBoard(BoardState boardState, String s) {
        Platform.runLater(() -> {
            drawBoardAll(gcBoard, config, boardState);
            text.setText(s);
        });
    }

    public static ServerBoardStateDisplay ServerBoardStateDisplayStart() {
        ServerBoardStateDisplay root = new ServerBoardStateDisplay();
        Scene scene = new Scene(root, 1000, 500);
        Stage stage = new Stage();

        stage.setTitle("Mensch Ärgere dich nicht");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setX(100);
        stage.setY(1100);
        return root;
    }
}
