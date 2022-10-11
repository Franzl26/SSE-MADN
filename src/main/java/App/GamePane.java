package App;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static App.BoardState.*;
import static App.FieldState.FIELD_NONE;

public class GamePane extends AnchorPane {

    private GameLogic logic;
    private final GraphicsContext gcBoard;
    private final GraphicsContext gcDice;
    private final GraphicsContext gcName;

    public GamePane() {
        setBackground(Background.fill(Color.LIGHTSLATEGRAY));
        Canvas nameCanvas = new Canvas(780, 50);
        gcName = nameCanvas.getGraphicsContext2D();

        Canvas diceCanvas = new Canvas(100, 100);
        gcDice = diceCanvas.getGraphicsContext2D();
        diceCanvas.setOnMouseClicked(e -> logic.onMouseClickedDice());

        Canvas boardCanvas = new Canvas(500, 500);
        gcBoard = boardCanvas.getGraphicsContext2D();
        boardCanvas.setOnMouseClicked(e -> logic.onMouseClickedField(e.getX(), e.getY()));

        Button spielVerlassenButton = new Button("Spiel verlassen");
        spielVerlassenButton.addEventHandler(ActionEvent.ACTION, e -> {

        });

        AnchorPane.setLeftAnchor(nameCanvas, 10.0);
        AnchorPane.setTopAnchor(nameCanvas, 10.0);
        AnchorPane.setLeftAnchor(diceCanvas, 10.0);
        AnchorPane.setBottomAnchor(diceCanvas, 210.0);
        AnchorPane.setLeftAnchor(boardCanvas, 120.0);
        AnchorPane.setBottomAnchor(boardCanvas, 10.0);
        AnchorPane.setRightAnchor(spielVerlassenButton, 10.0);
        AnchorPane.setBottomAnchor(spielVerlassenButton, 10.0);

        getChildren().addAll(nameCanvas, boardCanvas, diceCanvas, spielVerlassenButton);
        logic = new GameLogic(this);
    }

    public void drawDice(int number) {
        //int[][] points = new int[][]{{15, 15}, {35, 15}, {15, 25}, {25, 25}, {35, 25}, {15, 35}, {35, 35}};
        int[][] points = new int[][]{{20, 20}, {80, 20}, {20, 50}, {50, 50}, {80, 50}, {20, 80}, {80, 80}};
        int[][] toDraw = new int[][]{{}, {3}, {1, 5}, {1, 3, 5}, {0, 1, 5, 6}, {0, 1, 3, 5, 6}, {0, 1, 2, 4, 5, 6}, {0, 1, 2, 3, 4, 5, 6}};
        gcDice.setFill(Color.BLACK);
        gcDice.fillRect(0, 0, 100, 100);
        gcDice.setFill(Color.WHITE);
        gcDice.fillRect(3, 3, 94, 94);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        gcDice.setFill(Color.BLACK);
        for (int i = 0; i < toDraw[number].length; i++) {
            //gcDice.fillOval(points[toDraw[number][i]][0] - 5, points[toDraw[number][i]][1] - 5, 10, 10);
            gcDice.fillOval(points[toDraw[number][i]][0] - 10, points[toDraw[number][i]][1] - 10, 20, 20);
        }
    }

    public void drawBoard(BoardState board) {
        // Draw Background
        gcBoard.setFill(Color.RED);
        gcBoard.fillRect(0, 0, 500, 500);
        gcBoard.setFill(new Color(0.9, 0.9, 0.7, 1.0));
        gcBoard.fillRect(3, 3, 494, 494);

        FieldState[] state = board.getBoardState();

        // Draw Fields
        for (int i = 0; i < 72; i++) {
            drawBoardSingleField(state[i], i, false);
        }
    }

    public void drawBoardSingleField(FieldState state, int i, boolean highlight) {
        if (i < 32) drawBoardPersonal(state, i, highlight);
        else drawBoardPath(state, i, highlight);
    }

    public void drawBoardPersonal(FieldState state, int i, boolean highlight) {
        int r = circleRadius + 2;
        if (highlight) gcBoard.setFill(Color.PURPLE);
        else {
            gcBoard.setFill(new Color(0.9, 0.9, 0.7, 1.0));
            r++;
        }
        gcBoard.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);


        if (i < 4) gcBoard.setFill(Color.YELLOW);
        else if (i < 8) gcBoard.setFill(Color.GREEN);
        else if (i < 12) gcBoard.setFill(Color.RED);
        else if (i < 16) gcBoard.setFill(Color.BLUE);
        else if (i < 20) gcBoard.setFill(Color.YELLOW);
        else if (i < 24) gcBoard.setFill(Color.GREEN);
        else if (i < 28) gcBoard.setFill(Color.RED);
        else gcBoard.setFill(Color.BLUE);

        r = circleRadius - 4;
        gcBoard.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);
        if (state != FIELD_NONE) return;
        gcBoard.setFill(Color.LIGHTGRAY);
        r = circleRadius - 8;
        gcBoard.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);
    }

    public void drawBoardPath(FieldState state, int i, boolean highlight) {
        int r = circleRadius + 4;
        if (highlight) gcBoard.setFill(Color.PURPLE);
        else {
            gcBoard.setFill(new Color(0.9, 0.9, 0.7, 1.0));
            r++;
        }
        gcBoard.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);

        if (i == 32) gcBoard.setFill(Color.YELLOW);
        else if (i == 42) gcBoard.setFill(Color.GREEN);
        else if (i == 52) gcBoard.setFill(Color.RED);
        else if (i == 62) gcBoard.setFill(Color.BLUE);
        else gcBoard.setFill(Color.BLACK);
        r = circleRadius;
        gcBoard.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);

        switch (state) {
            case FIELD_NONE -> gcBoard.setFill(Color.WHITE);
            case FIELD_GREEN -> gcBoard.setFill(Color.GREEN);
            case FIELD_YELLOW -> gcBoard.setFill(Color.YELLOW);
            case FIELD_BLUE -> gcBoard.setFill(Color.BLUE);
            case FIELD_RED -> gcBoard.setFill(Color.RED);
        }
        r = circleRadius - 2;
        gcBoard.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);
    }

    public void drawNames(Players players, int turn) {
        gcName.setLineWidth(1.0);
        gcName.setFont(Font.font(40));
        //gcName.setFill(Color.GRAY);
        //gcName.fillRect(0,0,780,50);

        for (int i = 0; i < players.getCount(); i++) {
            Player p = players.getPlayer(i);
            gcName.setFill(p.getColor());
            gcName.fillText(p.getName(), i * 195 + 5, 40, 185);
            if (i == turn) gcName.fillRect(i * 195+5, 46, 185, 47);
        }
    }

    public static void GamePaneStart() {
        GamePane root = new GamePane();
        Scene scene = new Scene(root, 800, 600);
        Stage stage = new Stage();

        stage.setTitle("Mensch Ärgere dich nicht");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
