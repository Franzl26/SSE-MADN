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
        drawBoard(boardCanvas, board);

        GraphicsContext gcBoard = boardCanvas.getGraphicsContext2D();
        boardCanvas.setOnMouseClicked(e -> {
            for (int i = 0; i < 72; i++) {
                if (Math.hypot(e.getX() - pointCoordinates[i][0], e.getY() - pointCoordinates[i][1]) < circleRadius - 2) {
                    System.out.println("Field clicked: " + i);
                    if (!highlighted && board.getField(i) == FIELD_NONE) return;
                    if (!highlighted) {
                        highlighted = true;
                        highlightedField = i;
                        drawBoardSingleField(gcBoard, board.getField(i), i, true);
                    } else {
                        if (highlightedField == i) {
                            highlighted = false;
                            highlightedField = -1;
                            drawBoardSingleField(gcBoard, board.getField(i), i, false);
                        } else {
                            board.setField(i, board.getField(highlightedField));
                            board.setField(highlightedField, FIELD_NONE);
                            drawBoardSingleField(gcBoard, board.getField(i), i, false);
                            drawBoardSingleField(gcBoard, board.getField(highlightedField), highlightedField, false);
                            highlighted = false;
                            highlightedField = -1;
                        }
                    }
                    return;
                }
            }
            System.out.println("no Field hit");
        });

        Canvas diceCanvas = new Canvas(100, 100);
        GraphicsContext gcDice = diceCanvas.getGraphicsContext2D();
        drawDice(gcDice, 0);
        diceCanvas.setOnMouseClicked(e -> {
            drawDice(gcDice, (int) (Math.random() * 6 + 1));
        });

        // Fenster Zusammenstellen
        setCenter(boardCanvas);
        setLeft(diceCanvas);
    }

    private void drawDice(GraphicsContext gc, int number) {
        //int[][] points = new int[][]{{15, 15}, {35, 15}, {15, 25}, {25, 25}, {35, 25}, {15, 35}, {35, 35}};
        int[][] points = new int[][]{{20, 20}, {80, 20}, {20, 50}, {50, 50}, {80, 50}, {20, 80}, {80, 80}};
        int[][] toDraw = new int[][]{{}, {3}, {1, 5}, {1, 3, 5}, {0, 1, 5, 6}, {0, 1, 3, 5, 6}, {0, 1, 2, 4, 5, 6}};
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 100, 100);
        gc.setFill(Color.WHITE);
        gc.fillRect(3,3,94,94);

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        gc.setFill(Color.BLACK);
        for (int i = 0; i < toDraw[number].length; i++) {
            //gc.fillOval(points[toDraw[number][i]][0] - 5, points[toDraw[number][i]][1] - 5, 10, 10);
            gc.fillOval(points[toDraw[number][i]][0] - 10, points[toDraw[number][i]][1] - 10, 20, 20);
        }
        System.out.println(number);
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
        stage.setResizable(false);
        stage.show();
    }
}
