package dataAndMethods;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import static dataAndMethods.BoardConfiguration.clickRadius;
import static dataAndMethods.FieldState.FIELD_NONE;

public class BoardDrawer {
    public static void drawBoardAll(GraphicsContext gc, BoardConfiguration config, BoardState board) {
        //gc.setFill(Color.LIGHTSLATEGRAY);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 500, 500);
        gc.drawImage(config.board, 0, 0, 500, 500);
        FieldState[] state = board.getBoardState();
        for (int i = 0; i < 72; i++) {
            drawBoardSingleFieldAll(gc, config, state[i], i, false);
        }
    }

    public static void drawBoardSingleFieldAll(GraphicsContext gc, BoardConfiguration config, FieldState state, int i, boolean highlight) {
        Image image = switch (i) {
            case 32 -> config.path[0];
            case 42 -> config.path[1];
            case 52 -> config.path[2];
            case 62 -> config.path[3];
            default -> config.pathNormal;
        };
        if ((i >= 0 && i <= 3) || (i >= 16 && i <= 19)) image = config.personal[0];
        else if ((i >= 4 && i <= 7) || (i >= 20 && i <= 23)) image = config.personal[1];
        else if ((i >= 8 && i <= 11) || (i >= 24 && i <= 27)) image = config.personal[2];
        else if ((i >= 12 && i <= 15) || (i >= 28 && i <= 31)) image = config.personal[3];
        if (highlight) {
            switch (state) {
                case FIELD_FIGURE0 -> image = config.figureHigh[0];
                case FIELD_FIGURE1 -> image = config.figureHigh[1];
                case FIELD_FIGURE2 -> image = config.figureHigh[2];
                case FIELD_FIGURE3 -> image = config.figureHigh[3];
            }
        } else {
            switch (state) {
                case FIELD_FIGURE0 -> image = config.figure[0];
                case FIELD_FIGURE1 -> image = config.figure[1];
                case FIELD_FIGURE2 -> image = config.figure[2];
                case FIELD_FIGURE3 -> image = config.figure[3];
            }
        }

        ImageView iv = new ImageView(image);
        if (config.orientation[i][1] == 1 && state != FIELD_NONE) iv.setScaleX(-1);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        image = iv.snapshot(params, null);

        gc.drawImage(config.board, (config.pointCoordinates[i][0] - 20) * config.board.getWidth() / 500, (config.pointCoordinates[i][1] - 20) * config.board.getHeight() / 500, 40 * config.board.getWidth() / 500, 40 * config.board.getHeight() / 500, config.pointCoordinates[i][0] - 20, config.pointCoordinates[i][1] - 20, 40, 40);
        gc.save();
        gc.translate(config.pointCoordinates[i][0], config.pointCoordinates[i][1]);
        if (state != FIELD_NONE) gc.rotate(config.orientation[i][0]);
        gc.drawImage(image, -clickRadius, -clickRadius, 34, 34);
        gc.restore();
    }
}
