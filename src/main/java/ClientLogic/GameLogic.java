package ClientLogic;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import DataAndMethods.FieldState;
import DataAndMethods.MiscMethods;
import Dialogs.GamePane;

import static DataAndMethods.BoardConfiguration.clickRadius;
import static DataAndMethods.FieldState.*;

public class GameLogic {
    private final GamePane pane;
    private final BoardState board;
    public final BoardConfiguration boardConfig;

    private boolean highlighted = false;
    private int highlightedField = -1;

    public GameLogic(BoardConfiguration boardConfig) {
        this.boardConfig = boardConfig;
        pane = GamePane.GamePaneStart(this);

        board = new BoardState();
        board.reset();
        //board.fillWithOneTest();
        pane.drawBoard(board);
    }

    public void onMouseClickedField(double x, double y) {
        for (int i = 0; i < 72; i++) {
            if (Math.hypot(x - boardConfig.pointCoordinates[i][0], y - boardConfig.pointCoordinates[i][1]) < clickRadius - 2) {
                System.out.println("x: " + x + " y: " + y + " Field clicked: " + i);
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
                        testErlaubt(i); // todo remove

                        board.setField(i, board.getField(highlightedField));
                        board.setField(highlightedField, FIELD_NONE);
                        pane.drawBoardSingleField(board.getField(highlightedField), highlightedField, false);
                        pane.drawBoardSingleField(board.getField(i), i, false);
                        highlighted = false;
                        highlightedField = -1;
                    }
                }
                return;
            }
        }
    }

    public void onMouseClickedDice() {
        pane.drawDice((int) (Math.random() * 6 + 1));
    }

    public static void testBoardLogic() {
        //Stil auswählen
        new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard/"));
        //new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/StarWars"));
        //new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/Formula1"));
    }

    public void testButton() {
        System.out.println("\n\n\nPlayer         D1  D2  D3  D4  D5  D6");
        for (FieldState state : new FieldState[]{FIELD_FIGURE0, FIELD_FIGURE1, FIELD_FIGURE2, FIELD_FIGURE3}) {
            System.out.print(state + "  ");
            for (int i = 1; i < 7; i++) {
                int prio = MiscMethods.checkForPrioMove(board, state, i);
                System.out.print((prio < 10 && prio >= 0 ? " " : "") + prio + "  ");
            }
            System.out.println();
        }
    }

    public void testErlaubt(int to) {
        System.out.println("\n\n  1      2      3      4      5      6  ");
        for (int i = 1; i <= 6; i++) {
            boolean valid = MiscMethods.checkMoveValid(board, board.getField(highlightedField), highlightedField, to, i);
            System.out.print((valid ? " " : "") + valid + "  ");

        }
        System.out.println();
    }
}
