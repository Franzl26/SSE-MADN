package dataAndMethods;

import java.io.Serializable;
import java.util.Arrays;

import static dataAndMethods.FieldState.*;

public class BoardState implements Serializable {
    private FieldState[] board;

    private BoardState(FieldState[] fieldStates) {
        board = fieldStates;
    }

    public BoardState(int spielerAnz) {
        reset(spielerAnz);
    }

    public void reset(int spielerAnz) {
        board = new FieldState[72];
        Arrays.fill(board, 0, 72, FIELD_NONE);
        if (spielerAnz == 2) {
            Arrays.fill(board, 0, 4, FIELD_FIGURE0);
            Arrays.fill(board, 8, 12, FIELD_FIGURE2);
        } else {
            Arrays.fill(board, 0, 4, FIELD_FIGURE0);
            Arrays.fill(board, 4, 8, FIELD_FIGURE1);
            Arrays.fill(board, 8, 12, FIELD_FIGURE2);
            if (spielerAnz == 4) Arrays.fill(board, 12, 16, FIELD_FIGURE3);
        }
    }

    public void setField(int field, FieldState state) {
        if (state == null) throw new NullPointerException("State is not allowed to be null");
        board[field] = state;
    }

    public FieldState[] getBoardState() {
        return board.clone();
    }

    public FieldState getField(int i) {
        return board[i];
    }

    public static BoardState copyOf(BoardState boardState) {
        return new BoardState(boardState.getBoardState());
    }

    public void setCustom() {
        board = new FieldState[72];
        Arrays.fill(board, 0, 72, FIELD_NONE);
        board[32] = FIELD_FIGURE0;
        board[45] = FIELD_FIGURE0;
        board[18] = FIELD_FIGURE0;
        board[19] = FIELD_FIGURE0;
        board[48] = FIELD_FIGURE2;
        board[25] = FIELD_FIGURE2;
        board[26] = FIELD_FIGURE2;
        board[27] = FIELD_FIGURE2;
    }
}
