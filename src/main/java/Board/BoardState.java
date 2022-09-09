package Board;

import javafx.geometry.Point2D;

import java.util.Arrays;

import static Board.FieldState.*;

public class BoardState {
    public static final int[][] pointCoordinates = new int[][]{
            {50,50},{90,50},{50,90},{90,90}, // Yellow Start
            {410,50},{450,50},{410,90},{450,90}, // Green Start
            {410,410},{450,410},{410,450},{450,450}, // Red Start
            {50,410},{90,410},{50,450},{90,450}, // Blue Start
            {90,250},{130,250},{170,250},{210,250}, // Yellow Finish
            {250,90},{250,130},{250,170},{250,210}, // Green Finish
            {410,250},{370,250},{330,250},{290,250}, // Red Finish
            {250,410},{250,370},{250,330},{250,290}, // Blue Finish
            {50,210},{90,210},{130,210},{170,210},{210,210},{210,170},{210,130},{210,90},{210,50},{250,50}, // Yellow to Green
            {290,50},{290,90},{290,130},{290,170},{290,210},{330,210},{370,210},{410,210},{450,210},{450,250}, // Green to Red
            {450,290},{410,290},{370,290},{330,290},{290,290},{290,330},{290,370},{290,410},{290,450},{250,450}, // Red to Blue
            {210,450},{210,410},{210,370},{210,330},{210,290},{170,290},{130,290},{90,290},{50,290},{90,250} // Blue to Yellow
    };
    public static final int radius = 17;

    private FieldState[] board = new FieldState[72];

    public void reset() {
        board = new FieldState[72];
        Arrays.fill(board,0,3, FIELD_YELLOW);
        Arrays.fill(board,4,7, FIELD_GREEN);
        Arrays.fill(board,8,11, FIELD_RED);
        Arrays.fill(board,12,15, FIELD_BLUE);
    }

    public void setField(int field, FieldState state) {
        board[field] = state;
    }

    public FieldState[] getBoardState() {
        return board;
    }
}
