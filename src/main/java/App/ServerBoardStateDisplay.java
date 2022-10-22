package App;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import static DataAndMethods.BoardDrawer.drawBoardAll;

public class ServerBoardStateDisplay extends AnchorPane { // todo entfernen
    private final GraphicsContext gcBoard;
    private final BoardConfiguration config = BoardConfiguration.loadBoardKonfiguration("./resources/Server/designs/Standard/");

    public ServerBoardStateDisplay() {
        Canvas canvas = new Canvas(500,500);
        gcBoard = canvas.getGraphicsContext2D();

        getChildren().addAll(canvas);
    }

    public void drawBoard(BoardState boardState) {
        Platform.runLater(() -> {
            drawBoardAll(gcBoard, config, boardState);
        });
    }

    public static ServerBoardStateDisplay ServerBoardStateDisplayStart() {
        ServerBoardStateDisplay root = new ServerBoardStateDisplay();
        Scene scene = new Scene(root, 500, 500);
        Stage stage = new Stage();

        stage.setTitle("Mensch Ärgere dich nicht");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setX(1400);
        stage.setY(10);
        return root;
    }
}
