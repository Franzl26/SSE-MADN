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
        board[36] = FIELD_FIGURE0;
        board[37] = FIELD_FIGURE0;
        board[38] = FIELD_FIGURE0;
        board[52] = FIELD_FIGURE2;
        board[56] = FIELD_FIGURE2;
        board[57] = FIELD_FIGURE2;
        board[58] = FIELD_FIGURE2;
        board[42] = FIELD_FIGURE1;
        board[46] = FIELD_FIGURE1;
        board[47] = FIELD_FIGURE1;
        board[48] = FIELD_FIGURE1;
        board[62] = FIELD_FIGURE3;
        board[66] = FIELD_FIGURE3;
        board[67] = FIELD_FIGURE3;
        board[68] = FIELD_FIGURE3;
    }
}
