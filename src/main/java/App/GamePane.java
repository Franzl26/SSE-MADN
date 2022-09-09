package App;

import Board.BoardState;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GamePane extends BorderPane {
    public GamePane() {
        Canvas boardCanvas = new Canvas(600, 600);

        GraphicsContext gc = boardCanvas.getGraphicsContext2D();

        setCenter(boardCanvas);
        boardCanvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                System.out.println("x: " + e.getX() + " y: " + e.getY());
            }
        });

        BoardState board = new BoardState();
        board.reset();
        drawBoard(boardCanvas, board);
    }

    private void drawBoard(Canvas canvas, BoardState board) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillRect(0,0,500,500);
        gc.setFill(Color.BEIGE);
        gc.fillRect(3,3,494,494);

        // Draw Starts
        for (int i = 0; i < 16; i++) {

        }
    }

    public static void GamePaneStart() {
        GamePane root = new GamePane();
        Scene scene = new Scene(root, 1000, 700);
        Stage stage = new Stage();

        stage.setTitle("GamePane");
        stage.setScene(scene);
        stage.show();
    }
}
