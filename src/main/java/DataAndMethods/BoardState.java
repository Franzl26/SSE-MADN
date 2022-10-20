package DataAndMethods;

import java.io.Serializable;
import java.util.Arrays;

import static DataAndMethods.FieldState.*;

public class BoardState implements Serializable {
    private FieldState[] board = new FieldState[72];

    public void reset() {
        board = new FieldState[72];
        Arrays.fill(board, 0, 4, FIELD_FIGURE0);
        Arrays.fill(board, 4, 8, FIELD_FIGURE1);
        Arrays.fill(board, 8, 12, FIELD_FIGURE2);
        Arrays.fill(board, 12, 16, FIELD_FIGURE3);
        Arrays.fill(board, 16, 72, FIELD_NONE);
    }

    public void setField(int field, FieldState state) {
        if (state == null) throw new NullPointerException("State is not allowed to be null");
        board[field] = state;
    }

    public FieldState[] getBoardState() {
        return board;
    }

    public FieldState getField(int i) {
        return board[i];
    }

    public void fillWithOneTest() {
        Arrays.fill(board,0,72,FIELD_FIGURE0);
    }
}
