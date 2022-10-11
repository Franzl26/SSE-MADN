package App;

import javafx.scene.paint.Color;

import static App.BoardState.*;
import static App.FieldState.*;

public class GameLogic {
    private GamePane pane;
    private BoardState board;

    private boolean highlighted = false;
    private int highlightedField = -1;

    public GameLogic(GamePane pane) {
        if (pane == null) throw new IllegalArgumentException("pane must not be null");
        this.pane = pane;

        board = new BoardState();
        board.reset();
        pane.drawBoard(board);
        pane.drawDice(7);

        Players players = new Players();
        players.addPlayer("5char", Color.YELLOW);
        players.addPlayer("10 Zeichen", Color.RED);
        players.addPlayer("15 Zeichen abcd", Color.BLUE);
        players.addPlayer("20 Zeichen abcdefghi", Color.GREEN);
        pane.drawNames(players,2);
    }

    public void onMouseClickedField(double x, double y) {
        for (int i = 0; i < 72; i++) {
            if (Math.hypot(x - pointCoordinates[i][0], y - pointCoordinates[i][1]) < circleRadius - 2) {
                System.out.println("Field clicked: " + i);
                if (!highlighted && board.getField(i) == FIELD_NONE) return;
                if (!highlighted) {
                    highlighted = true;
                    highlightedField = i;
                    pane.drawBoardSingleField(board.getField(i), i, true);
                } else {
                    if (highlightedField == i) {
                        highlighted = false;
                        highlightedField = -1;
                        pane.drawBoardSingleField(board.getField(i), i, false);
                    } else {
                        board.setField(i, board.getField(highlightedField));
                        board.setField(highlightedField, FIELD_NONE);
                        pane.drawBoardSingleField(board.getField(i), i, false);
                        pane.drawBoardSingleField(board.getField(highlightedField), highlightedField, false);
                        highlighted = false;
                        highlightedField = -1;
                    }
                }
                return;
            }
        }
        System.out.println("no Field hit");
    }

    public void onMouseClickedDice() {
        pane.drawDice((int) (Math.random()*6+1));
    }
}
