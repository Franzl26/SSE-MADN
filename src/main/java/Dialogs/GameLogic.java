package Dialogs;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import DataAndMethods.GameStatistics;
import RMIInterfaces.LoggedInInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class GameLogic {
    private final GamePane pane;
    private final LoggedInInterface lii;
    public final BoardConfiguration boardConfig;

    private boolean highlighted = false;
    private int highlightedField = -1;


    /**
     * -1 nicht würfeln, 0 würfeln, 1-6 gewürfelt
     */
    private int gewuerfelt = -1;

    public GameLogic(LoggedInInterface lii, String design) {
        this.lii = lii;
        boardConfig = BoardConfiguration.loadBoardKonfiguration("./resources/designs/"+design);
        pane = GamePane.GamePaneStart(this);
    }

    // raus gehende Funktionen
    public void onMouseClickedField(double x, double y) { // todo
        /*for (int i = 0; i < 72; i++) {
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
        }*/
    }

    public void onMouseClickedDice() { // todo
        if (gewuerfelt != 0) return;
        try {
            gewuerfelt = lii.throwDice();
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }

    }

    // rein kommenden Funktionen
    public void displayNewState(BoardState state, int[] changed, String[] names, int turn) {
        Platform.runLater(() -> {
            pane.hideGif();
            pane.drawDice(0);
            pane.drawNames(names, turn);
            if (changed == null) pane.drawBoard(state);
            else for (int i : changed) pane.drawBoardSingleField(state.getField(i), i, false);
        });
    }

    public void displayDice(int number) {
        pane.drawDice(number);
    }

    public void rollDiceOver(int wurf) {
        gewuerfelt = wurf;
        new Alert(Alert.AlertType.INFORMATION, "Deine Zeit zum Würfeln ist abgelaufen, der Server hat für dich gewürfelt.").showAndWait();
    }

    public void displayGif() {
        pane.showGif();
    }

    public void moveFigureOver() {
        gewuerfelt = -1;
        new Alert(Alert.AlertType.INFORMATION, "Deine Zeit zum Ziehen ist abgelaufen, der Server hat für dich gezogen,").showAndWait();
    }

    public void showPane() {
        ((Stage) pane.getScene().getWindow()).show();
    }

    public void closePane() {
        ((Stage) pane.getScene().getWindow()).close();
    }

    public void spielVerlassen() {
        try {
            GameStatistics stats = lii.getStatistics();
            lii.leaveGame();
            GameStatisticsPane pane = GameStatisticsPane.GameStatisticsPaneStart();
            pane.drawStatistics(stats);
            ((Stage) pane.getScene().getWindow()).show();
        } catch (RemoteException e) {
            e.printStackTrace(System.out);
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
        }
    }
}
