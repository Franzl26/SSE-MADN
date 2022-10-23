package App;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

import static DataAndMethods.BoardDrawer.drawBoardAll;

public class DebugPane extends AnchorPane {
    private final GraphicsContext gc;
    private BoardState[] boardStates;
    private int anzeige = 0;
    private BoardConfiguration config = BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard/");

    public DebugPane() {
        Canvas canvas = new Canvas(500,500);
        gc = canvas.getGraphicsContext2D();

        boardStates = readBoardState();
        Button vor = new Button("vor");
        vor.addEventHandler(ActionEvent.ACTION, e -> {
            anzeige++;
            if (anzeige == boardStates.length) anzeige--;
            boardAnzeigen();
        });
        Button zurueck = new Button("zurueck");
        zurueck.addEventHandler(ActionEvent.ACTION, e -> {
            anzeige--;
            if (anzeige < 0) anzeige = 0;
            boardAnzeigen();
        });
        getChildren().addAll(canvas,vor,zurueck);
    }

    private void boardAnzeigen() {
        System.out.println(anzeige);
        drawBoardAll(gc, config, boardStates[anzeige]);
    }

    private BoardState[] readBoardState() {
        File f = new File("./resources/boards");
        ArrayList<BoardState> state = new ArrayList<>();
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f))) {
            while (true) {
                state.add((BoardState) is.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            //throw new RuntimeException(e);
        }
        return state.toArray(new BoardState[0]);
    }

    public static void DebugPaneStart() {
        DebugPane root = new DebugPane();
        Scene scene = new Scene(root, 1000, 600);
        Stage stage = new Stage();

        stage.setTitle("Mensch Ärgere dich nicht");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
