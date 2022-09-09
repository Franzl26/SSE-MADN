package App;

import Board.BoardState;
import Board.FieldState;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static Board.BoardState.*;
import static Board.FieldState.FIELD_NONE;

public class GamePane extends BorderPane {
    private boolean highlighted = false;
    private int highlightedField = -1;
    public GamePane() {
        Canvas boardCanvas = new Canvas(500, 500);

        BoardState board = new BoardState();
        board.reset();
        board.setField(0, FIELD_NONE);
        board.setField(4, FIELD_NONE);
        board.setField(8, FIELD_NONE);
        board.setField(12, FIELD_NONE);
        drawBoard(boardCanvas, board);

        GraphicsContext gc = boardCanvas.getGraphicsContext2D();
        boardCanvas.setOnMouseClicked(e -> {
            for (int i = 0; i < 72; i++) {
                if (Math.hypot(e.getX() - pointCoordinates[i][0], e.getY() - pointCoordinates[i][1]) < circleRadius - 2) {
                    System.out.println("Field clicked: " + i);
                    if (!highlighted && board.getField(i) == FIELD_NONE) return;
                    if (!highlighted) {
                        highlighted = true;
                        highlightedField = i;
                        drawBoardSingleField(gc, board.getField(i), i, true);
                    } else {
                        if (highlightedField == i) {
                            highlighted = false;
                            highlightedField = -1;
                            drawBoardSingleField(gc, board.getField(i), i, false);
                        } else {
                            board.setField(i, board.getField(highlightedField));
                            board.setField(highlightedField, FIELD_NONE);
                            drawBoardSingleField(gc, board.getField(i), i, false);
                            drawBoardSingleField(gc, board.getField(highlightedField), highlightedField, false);
                            highlighted = false;
                            highlightedField = -1;
                        }
                    }
                    return;
                }
            }
            System.out.println("no Field hit");
        });

        // Fenster Zusammenstellen
        setCenter(boardCanvas);
    }

    private void drawBoard(Canvas canvas, BoardState board) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw Background
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, 500, 500);
        gc.setFill(new Color(0.9, 0.9, 0.7, 1.0));
        gc.fillRect(3, 3, 494, 494);

        FieldState[] state = board.getBoardState();

        // Draw Fields
        for (int i = 0; i < 72; i++) {
            drawBoardSingleField(gc, state[i], i, false);
        }
    }

    private void drawBoardSingleField(GraphicsContext gc, FieldState state, int i, boolean highlight) {
        if (i < 32) drawBoardPersonal(gc, state, i, highlight);
        else drawBoardPath(gc, state, i, highlight);
    }

    private void drawBoardPersonal(GraphicsContext gc, FieldState state, int i, boolean highlight) {
        int r = circleRadius + 2;
        if (highlight) gc.setFill(Color.PURPLE);
        else {
            gc.setFill(new Color(0.9, 0.9, 0.7, 1.0));
            r++;
        }
        gc.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);


        if (i < 4) gc.setFill(Color.YELLOW);
        else if (i < 8) gc.setFill(Color.GREEN);
        else if (i < 12) gc.setFill(Color.RED);
        else if (i < 16) gc.setFill(Color.BLUE);
        else if (i < 20) gc.setFill(Color.YELLOW);
        else if (i < 24) gc.setFill(Color.GREEN);
        else if (i < 28) gc.setFill(Color.RED);
        else gc.setFill(Color.BLUE);

        r = circleRadius - 4;
        gc.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);
        if (state != FIELD_NONE) return;
        gc.setFill(Color.LIGHTGRAY);
        r = circleRadius - 8;
        gc.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);
    }

    private void drawBoardPath(GraphicsContext gc, FieldState state, int i, boolean highlight) {
        int r = circleRadius + 4;
        if (highlight) gc.setFill(Color.PURPLE);
        else {
            gc.setFill(new Color(0.9, 0.9, 0.7, 1.0));
            r++;
        }
        gc.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);

        if (i == 32) gc.setFill(Color.YELLOW);
        else if (i == 42) gc.setFill(Color.GREEN);
        else if (i == 52) gc.setFill(Color.RED);
        else if (i == 62) gc.setFill(Color.BLUE);
        else gc.setFill(Color.BLACK);
        r = circleRadius;
        gc.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);

        switch (state) {
            case FIELD_NONE -> gc.setFill(Color.WHITE);
            case FIELD_GREEN -> gc.setFill(Color.GREEN);
            case FIELD_YELLOW -> gc.setFill(Color.YELLOW);
            case FIELD_BLUE -> gc.setFill(Color.BLUE);
            case FIELD_RED -> gc.setFill(Color.RED);
        }
        r = circleRadius - 2;
        gc.fillOval(pointCoordinates[i][0] - r, pointCoordinates[i][1] - r, r * 2, r * 2);
    }

    public static void GamePaneStart() {
        GamePane root = new GamePane();
        Scene scene = new Scene(root, 600, 600);
        Stage stage = new Stage();

        stage.setTitle("GamePane");
        stage.setScene(scene);
        stage.show();
    }
}
