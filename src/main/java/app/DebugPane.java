package app;

import dataAndMethods.BoardConfiguration;
import dataAndMethods.BoardState;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;

import static dataAndMethods.BoardDrawer.drawBoardAll;

public class DebugPane extends AnchorPane {
    private final GraphicsContext gc;
    private final BoardState[] boardStates;
    private int anzeige = 0;
    private final BoardConfiguration config = BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard/");

    public DebugPane() {
        Canvas canvas = new Canvas(500, 500);
        gc = canvas.getGraphicsContext2D();

        boardStates = readBoardState();
        System.out.println(boardStates.length);

        Button vor = new Button("vor");
        vor.addEventHandler(ActionEvent.ACTION, e -> {
            anzeige++;
            if (anzeige == boardStates.length || boardStates[anzeige] == null) anzeige--;
            boardAnzeigen();
        });
        Button zurueck = new Button("zurueck");
        zurueck.addEventHandler(ActionEvent.ACTION, e -> {
            anzeige--;
            if (anzeige < 0) anzeige = 0;
            boardAnzeigen();
        });
        AnchorPane.setBottomAnchor(vor, 10.0);
        AnchorPane.setBottomAnchor(zurueck, 10.0);
        AnchorPane.setLeftAnchor(zurueck, 10.0);
        AnchorPane.setRightAnchor(vor, 10.0);
        getChildren().addAll(canvas, vor, zurueck);
        boardAnzeigen();
    }

    private void boardAnzeigen() {
        System.out.println(anzeige);
        drawBoardAll(gc, config, boardStates[anzeige]);
    }

    private BoardState[] readBoardState() {
        File f = new File("./resources/boards");
        BoardState[] state = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(f))) {
            state = (BoardState[]) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        return state;
    }

    public static void DebugPaneStart() {
        DebugPane root = new DebugPane();
        Scene scene = new Scene(root, 500, 550);
        Stage stage = new Stage();

        stage.setTitle("Mensch Ärgere dich nicht");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
