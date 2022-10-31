package dialogs;

import dataAndMethods.BoardConfiguration;
import dataAndMethods.BoardState;
import dataAndMethods.GameStatistics;
import javafx.application.Platform;
import javafx.stage.Stage;
import rmiInterfaces.LoggedInInterface;

import java.rmi.RemoteException;

import static dataAndMethods.BoardConfiguration.clickRadius;
import static dataAndMethods.FieldState.FIELD_NONE;

public class GameLogic {
    private final GamePane pane;
    private final LoggedInInterface lii;
    public final BoardConfiguration boardConfig;

    private boolean highlighted = false;
    private int highlightedField = -1;
    private BoardState boardState;

    public GameLogic(LoggedInInterface lii, String design) {
        this.lii = lii;
        boardConfig = BoardConfiguration.loadBoardKonfiguration("./resources/designs/" + design);
        pane = GamePane.GamePaneStart(this);
    }

    // raus gehende Funktionen
    public void onMouseClickedField(double x, double y) {
        for (int i = 0; i < 72; i++) {
            if (Math.hypot(x - boardConfig.pointCoordinates[i][0], y - boardConfig.pointCoordinates[i][1]) < clickRadius - 2) {
                //System.out.println("Field clicked: " + i);
                if (!highlighted && boardState.getField(i) == FIELD_NONE) return;
                if (!highlighted) {
                    highlighted = true;
                    highlightedField = i;
                    pane.drawBoardSingleField(boardState.getField(i), i, true);
                } else {
                    if (highlightedField == i) {
                        highlighted = false;
                        highlightedField = -1;
                        pane.drawBoardSingleField(boardState.getField(i), i, false);
                    } else {
                        try {
                            pane.drawBoardSingleField(boardState.getField(highlightedField), highlightedField, false);
                            int ret = lii.submitMove(highlightedField, i);
                            if (ret == -1) {
                                Meldungen.zeigeInformation("Fehlerhafter Zug","Fehlerhafter Zug. Bitte nochmal setzen.");
                            } else if (ret == -2) {
                                Meldungen.zeigeInformation("Prio Zug missachtet","Du hast einen PrioZug missachtet, die entsprechende Figur wurde geschlagen");
                            } else if (ret == -3) {
                                Meldungen.zeigeInformation("Nicht an der Reihe","Du bist nicht dran du Pflaume");
                            } else if (ret == -4) {
                                Meldungen.zeigeInformation("Nicht gewürfelt", "Du hast noch nicht gewürfelt");
                            }
                        } catch (RemoteException e) {
                            Meldungen.kommunikationAbgebrochen();
                            System.exit(0);
                        }

                        highlighted = false;
                        highlightedField = -1;
                    }
                }
                return;
            }
        }
    }

    public void onMouseClickedDice() {
        try {
            int ret = lii.throwDice();
            if (ret == -2)
                Meldungen.zeigeInformation("Schon gewürfelt","Du hast schon gewürfelt, erstmal ziehen");
        } catch (RemoteException e) {
            Meldungen.kommunikationAbgebrochen();
            System.exit(0);
        }
    }

    // rein kommenden Funktionen
    public void displayNewState(BoardState state, int[] changed, String[] names, int turn) {
        if (state != null) boardState = state;
        Platform.runLater(() -> {
            pane.hideGif();
            pane.drawDice(0);
            pane.drawNames(names, turn);
            if (state == null) return;
            if (changed == null) pane.drawBoard(state);
            else for (int i : changed) if (i != -1) pane.drawBoardSingleField(state.getField(i), i, false);
        });
    }

    public void displayDice(int number) {
        Platform.runLater(() -> {
            if (number > 0) pane.drawDice(number);
        });
    }

    public void rollDiceOver() {
        Platform.runLater(() -> Meldungen.zeigeInformation("Zeit abgelaufen","Deine Zeit zum Würfeln ist abgelaufen, der Server hat für dich gewürfelt und gezogen."));
    }

    public void displayGif() {
        Platform.runLater(pane::showGif);
    }

    public void moveFigureOver() {
        Platform.runLater(() -> Meldungen.zeigeInformation("Zeit abgelaufen","Deine Zeit zum Ziehen ist abgelaufen, der Server hat für dich gezogen."));
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
            Meldungen.kommunikationAbgebrochen();
            System.exit(0);
        }
    }
}
