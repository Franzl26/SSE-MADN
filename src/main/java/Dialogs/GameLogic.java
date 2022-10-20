package Dialogs;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import RMIInterfaces.GameInterface;
import RMIInterfaces.UpdateGameInterface;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.rmi.RemoteException;

import static DataAndMethods.BoardConfiguration.clickRadius;
import static DataAndMethods.FieldState.*;

public class GameLogic {
    private final GamePane pane;
    private final BoardState board;
    public BoardConfiguration boardConfig;

    private boolean highlighted = false;
    private int highlightedField = -1;


    private final UpdateGameInterface ugi;
    private GameInterface gameInterface;
    private boolean wuerfelnErlaubt = false;
    private boolean figurBewegenErlaubt = false;

    public GameLogic() {
        try {
            ugi = new UpdateGameObject(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        pane = GamePane.GamePaneStart(this);

        board = new BoardState();
        board.reset();
        pane.drawBoard(board);
    }

    // raus gehende Funktionen
    public void onMouseClickedField(double x, double y) { // todo
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

    public void onMouseClickedDice() { // todo
        if (!wuerfelnErlaubt) return;
        wuerfelnErlaubt = false;
        pane.drawDice((int) (Math.random() * 6 + 1));
    }

    // rein kommenden Funktionen
    public void displayNewState(BoardState state, int[] changed, String[] names, int turn) {
        pane.hideGif();
        pane.drawDice(0);
        pane.drawNames(names,turn);
        for (int i : changed) {
            pane.drawBoardSingleField(state.getField(i), i, false);
        }
    }

    public void displayDice(int number) {
        pane.drawDice(number);
    }

    public void rollDiceOver() {
        wuerfelnErlaubt = false;
        new Alert(Alert.AlertType.INFORMATION,"Deine Zeit zum Würfeln ist abgelaufen, der Server hat für dich gewürfelt.").showAndWait();
    }

    public void displayGif() {
        pane.showGif();
    }

    public void moveFigureOver() {
        figurBewegenErlaubt = false;
        new Alert(Alert.AlertType.INFORMATION,"Deine Zeit zum Ziehen ist abgelaufen, der Server hat für dich gezogen,").showAndWait();
    }

    public UpdateGameInterface getUGI() {
        return ugi;
    }

    public void showPane() {
        ((Stage)pane.getScene().getWindow()).show();
    }

    public void closePane() {
        ((Stage)pane.getScene().getWindow()).close();
    }

    public void setGameInterface(GameInterface game) {
        gameInterface = game;
    }

    public static void testBoardLogic() { // todo entfernen
        //Stil auswählen
        //new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard/"));
        //new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/StarWars"));
        //new GameLogic(BoardConfiguration.loadBoardKonfiguration("./resources/designs/Formula1"));
    }
}
