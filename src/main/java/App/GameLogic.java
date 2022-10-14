package App;

import Dialogs.GamePane;

import static App.BoardConfiguration.clickRadius;
import static App.FieldState.*;

public class GameLogic {
    private final GamePane pane;
    private final BoardState board;

    private boolean highlighted = false;
    private int highlightedField = -1;
    public final BoardConfiguration boardConfig;

    public GameLogic(BoardConfiguration boardConfig) {
        this.boardConfig = boardConfig;
        pane = GamePane.GamePaneStart(this);

        board = new BoardState();
        board.reset();
        board.fillWithOneTest();
        pane.drawBoard(board);
    }

    public void onMouseClickedField(double x, double y) {
        for (int i = 0; i < 72; i++) {
            if (Math.hypot(x - boardConfig.pointCoordinates[i][0], y - boardConfig.pointCoordinates[i][1]) < clickRadius - 2) {
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

    public static void testBoardLogic() {
        //Stil auswählen
        //new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard/"));
        //new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/StarWars"));
        new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/Formula1"));
    }
}
