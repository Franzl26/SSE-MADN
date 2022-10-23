package Server;

import App.ServerBoardStateDisplay;
import DataAndMethods.BoardState;
import DataAndMethods.FieldState;
import DataAndMethods.GameStatistics;
import RMIInterfaces.GameInterface;
import RMIInterfaces.LoggedInInterface;
import RMIInterfaces.UpdateGameInterface;
import javafx.application.Platform;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import static DataAndMethods.FieldState.*;
import static DataAndMethods.MiscMethods.*;
import static java.lang.Thread.sleep;

// todo Timer bei zu langsam

public class GameObject extends UnicastRemoteObject implements GameInterface {
    private static final int BOT_WAIT_WUERFELN = 1000;
    private static final int BOT_WAIT_ZIEHEN = 2000;

    private final GameStatistics gameStatistics;

    private final LoggedInInterface[] clients;
    private final UpdateGameInterface[] clientsUpdate;
    private final String[] names;
    private final FieldState[] fields;
    private final boolean[] finished;
    private int anzahlFinished = 0;

    private final int spielerAnzahl;
    private final BoardState boardState;
    private int aktiverSpieler = -1;
    private int zahlGewuerfelt = 0;
    private int anzahlWuerfeln = -1;

    // todo entfernen
    //private final int[] wuerfel = new int[]{6, 4, 1, 6, 4, 1, 4, 3, 3};
    private final int[] wuerfel = new int[]{6};
    private int wuerfelCount = 0;

    private File fBoard = new File("./resources/boards");
    private File fText = new File("./resources/text.txt");

    protected GameObject(LoggedInInterface[] lii, int spielerUndBotsAnz) throws RemoteException {
        spielerAnzahl = spielerUndBotsAnz;
        clients = lii.clone();

        //clients[0] = null; // todo entfernen

        clientsUpdate = new UpdateGameInterface[spielerAnzahl];
        names = new String[spielerAnzahl];
        fields = new FieldState[spielerAnzahl];
        finished = new boolean[spielerAnzahl];

        if (spielerAnzahl == 2) {
            names[0] = (clients[0] == null ? "Bot0" : clients[0].getUsername());
            names[1] = (clients[1] == null ? "Bot1" : clients[1].getUsername());
            fields[0] = FIELD_FIGURE0;
            fields[1] = FIELD_FIGURE2;
        } else {
            for (int i = 0; i < spielerAnzahl; i++) {
                names[i] = (clients[i] == null ? "Bot" + i : clients[i].getUsername());
                fields[i] = switch (i) {
                    case 0 -> FIELD_FIGURE0;
                    case 1 -> FIELD_FIGURE1;
                    case 2 -> FIELD_FIGURE2;
                    default -> FIELD_FIGURE3;
                };
            }
        }
        gameStatistics = new GameStatistics(names);
        boardState = new BoardState(spielerAnzahl);

        //boardState.setCustom(); // todo entfernen

        Timer t = new Timer("startGame");
        t.schedule(new StartGame(), 500);


        // todo entfernen
        Platform.runLater(() -> {
            display = ServerBoardStateDisplay.ServerBoardStateDisplayStart();
            Timer t2 = new Timer("boardZeichen");
            t2.schedule(new UpdateDisplay(), 0, 500);
        });
        boardWriteInit();
    }

    public synchronized void checkPlayerIn(LoggedInInterface lii, UpdateGameInterface ugi) {
        int i = isInGame(lii);
        if (i == -1) return;
        clientsUpdate[i] = ugi;
        System.out.println("ugi set for player " + i);
        displayNewStateIntern(lii, ugi, boardState, null, names, -1);
    }

    @Override
    public synchronized int submitMove(LoggedInInterface lii, int from, int to) throws RemoteException {
        int i = isInGame(lii);
        if (i == -1 || aktiverSpieler != i) return -3;
        FieldState field = fields[aktiverSpieler];
        if (!checkMoveValid(boardState, field, from, to, zahlGewuerfelt)) return -1;
        // prüfen ob rausgesetzt bei 6
        /*if (zahlGewuerfelt == 6) {
            if (field == FIELD_FIGURE0) {
                if ((boardState.getField(0) == FIELD_FIGURE0 || boardState.getField(1) == FIELD_FIGURE0 ||
                        boardState.getField(2) == FIELD_FIGURE0 || boardState.getField(3) == FIELD_FIGURE0))
            }
        }*/
        int[] prio = checkForPrioMove(boardState, field, zahlGewuerfelt);
        if (prio != null && prio[0] < 16 && from > 15) return -1;
        return submitMoveIntern(from, to);
    }

    /**
     * move muss erlaubt sein, wird nur noch gesetzt + ggf Punish + Spieler informiert
     */
    private synchronized int submitMoveIntern(int from, int to) {
        System.out.println("Submit " + names[aktiverSpieler] + ": " + from + " -> " + to);
        int[] changed = new int[]{from, to, -1, -1, -1}; // from, to, strafe in loch, jmd geschlagen, strafe Figur die weg
        // wenn geschlagen zurücksetzen
        FieldState fieldStateFrom = boardState.getField(from);
        if (boardState.getField(to) != FIELD_NONE) {
            gameStatistics.incAndereGeschlagen(aktiverSpieler);
            for (int i = 0; i < spielerAnzahl; i++) {
                if (fields[i] == boardState.getField(to)) {
                    gameStatistics.incGeschlagenWorden(i);
                    break;
                }
            }
            changed[3] = figurZurueckAufStartpositionen(to);
            System.out.println("----------------------------------geschlagen-----------------------------");
        }

        // bestrafen
        // abrücken
        if (from != 32 && fieldStateFrom == FIELD_FIGURE0 && boardState.getField(32) == FIELD_FIGURE0 && boardState.getField(32 + zahlGewuerfelt) != FIELD_FIGURE0) {
            changed[2] = figurZurueckAufStartpositionen(32);
            changed[4] = 32;
        }
        if (from != 42 && fieldStateFrom == FIELD_FIGURE1 && boardState.getField(42) == FIELD_FIGURE1 && boardState.getField(42 + zahlGewuerfelt) != FIELD_FIGURE1) {
            changed[2] = figurZurueckAufStartpositionen(42);
            changed[4] = 42;
        }
        if (from != 52 && fieldStateFrom == FIELD_FIGURE2 && boardState.getField(52) == FIELD_FIGURE2 && boardState.getField(52 + zahlGewuerfelt) != FIELD_FIGURE2) {
            changed[2] = figurZurueckAufStartpositionen(52);
            changed[4] = 52;
        }
        if (from != 62 && fieldStateFrom == FIELD_FIGURE3 && boardState.getField(62) == FIELD_FIGURE3 && boardState.getField(62 + zahlGewuerfelt) != FIELD_FIGURE3) {
            changed[2] = figurZurueckAufStartpositionen(62);
            changed[4] = 62;
        }
        // schlagen
        for (int i = 39; i >= 0; i--) {
            int intFieldTo = (i + zahlGewuerfelt) % 40 + 32;
            if ((i + 32) != from && boardState.getField(i + 32) == fieldStateFrom && boardState.getField(intFieldTo) != fieldStateFrom && boardState.getField(intFieldTo) != FIELD_NONE) {
                // nicht über Start beachten
                if (fieldStateFrom == FIELD_FIGURE0 && (i + zahlGewuerfelt) >= 40) continue;
                if (fieldStateFrom == FIELD_FIGURE1 && (i + zahlGewuerfelt) >= 10 && i < 10) continue;
                if (fieldStateFrom == FIELD_FIGURE2 && (i + zahlGewuerfelt) >= 20 && i < 20) continue;
                if (fieldStateFrom == FIELD_FIGURE3 && (i + zahlGewuerfelt) >= 30 && i < 30) continue;
                changed[2] = figurZurueckAufStartpositionen(i + 32);
            }
        }
        if (changed[2] != -1) gameStatistics.incPrioZugIgnoriert(aktiverSpieler);
        boardState.setField(to, fieldStateFrom);
        boardState.setField(from, FIELD_NONE);

        // gewonnen Erkennung
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE0 && boardState.getField(16) == FIELD_FIGURE0
                && boardState.getField(17) == FIELD_FIGURE0 && boardState.getField(18) == FIELD_FIGURE0
                && boardState.getField(19) == FIELD_FIGURE0) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE1 && boardState.getField(20) == FIELD_FIGURE1
                && boardState.getField(21) == FIELD_FIGURE1 && boardState.getField(22) == FIELD_FIGURE1
                && boardState.getField(23) == FIELD_FIGURE1) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE2 && boardState.getField(24) == FIELD_FIGURE2
                && boardState.getField(25) == FIELD_FIGURE2 && boardState.getField(26) == FIELD_FIGURE2
                && boardState.getField(27) == FIELD_FIGURE2) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE3 && boardState.getField(28) == FIELD_FIGURE3
                && boardState.getField(29) == FIELD_FIGURE3 && boardState.getField(30) == FIELD_FIGURE3
                && boardState.getField(31) == FIELD_FIGURE3) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        boardWrite("throw diceIntern: " + names[aktiverSpieler] + " : " + zahlGewuerfelt+"\nSubmit " + names[aktiverSpieler] + ": " + from + " -> " + to); // todo entfernen
        displayNewStateAll(boardState, changed, names, aktiverSpieler);
        if (anzahlWuerfeln == 0) {
            nextPlayer(new int[]{});
        }
        zahlGewuerfelt = -1;
        return (changed[2] == -1 ? 1 : -2);
    }

    @Override
    public synchronized int throwDice(LoggedInInterface lii) throws RemoteException {
        int i = isInGame(lii);
        if (i == -1 || i != aktiverSpieler) return -1;
        return throwDiceIntern();
    }

    private synchronized int throwDiceIntern() {
        if (zahlGewuerfelt > 0) {
            if (getValidMove(boardState, fields[aktiverSpieler], zahlGewuerfelt)[0] != -1) return -2;
        }
        if (anzahlWuerfeln == 0) return -3;

        if (wuerfelCount < wuerfel.length) {
            zahlGewuerfelt = wuerfel[wuerfelCount++];   // todo entfernen
        } else {
            zahlGewuerfelt = (int) (Math.random() * 6 + 1);
        }

        gameStatistics.incZahlGewuerfelt(aktiverSpieler, zahlGewuerfelt - 1);
        System.out.println("throw diceIntern: " + names[aktiverSpieler] + " : " + zahlGewuerfelt);
        if (zahlGewuerfelt == 6)
            System.out.println("--------------------------------6 gewuerfelt -------------------------------------");
        anzahlWuerfeln--;
        displayDiceAll(zahlGewuerfelt);
        if (zahlGewuerfelt == 6) {
            anzahlWuerfeln = 1;
        }
        if (anzahlWuerfeln == 0 && getValidMove(boardState, fields[aktiverSpieler], zahlGewuerfelt)[0] == -1) {
            zahlGewuerfelt = -1;
            nextPlayer(new int[]{});
            //displayNewStateAll(boardState, new int[]{}, names, aktiverSpieler);
        }
        return zahlGewuerfelt;
    }

    private void nextPlayer(int[] changed) {
        aktiverSpieler = (aktiverSpieler + 1) % spielerAnzahl;

        new Thread(() -> {
            try {
                if (anzahlFinished == spielerAnzahl - 1) {
                    System.out.println("Spiel zu Ende");
                    aktiverSpieler = -1;
                    zahlGewuerfelt = -1;
                    anzahlWuerfeln = -1;
                    Thread.currentThread().interrupt();
                    return;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("schlafen unterbrochen");
                }
                while (finished[aktiverSpieler]) aktiverSpieler = (aktiverSpieler + 1) % spielerAnzahl;
                anzahlWuerfeln = getAnzahlWuerfelnNext(aktiverSpieler);
                if (anzahlWuerfeln == 3)
                    System.out.println("--------------------------------3 mal Wuerfeln----------------------------------------");
                if (changed != null) displayNewStateAll(boardState, changed, names, aktiverSpieler);
                //System.out.println("spieler dran: " + names[aktiverSpieler]);
                if (clients[aktiverSpieler] != null) sendYourTurn(aktiverSpieler);
                else doBotMove(aktiverSpieler);
            } catch (RuntimeException e) {
                System.err.println("beendet");
            }
        }).start();

    }

    private int getAnzahlWuerfelnNext(int spielerNext) {
        FieldState fieldState = fields[spielerNext];
        if (fieldState == FIELD_FIGURE0) {
            int countFehlen = 0;
            for (int i = 0; i < 4; i++) if (boardState.getField(i) == FIELD_NONE) countFehlen++;
            for (int i = 0; i < countFehlen; i++) if (boardState.getField(19 - i) == FIELD_NONE) return 1;
            return 3;
        }
        if (fieldState == FIELD_FIGURE1) {
            int countFehlen = 0;
            for (int i = 4; i < 8; i++) if (boardState.getField(i) == FIELD_NONE) countFehlen++;
            for (int i = 0; i < countFehlen; i++) if (boardState.getField(23 - i) == FIELD_NONE) return 1;
            return 3;
        }
        if (fieldState == FIELD_FIGURE2) {
            int countFehlen = 0;
            for (int i = 8; i < 12; i++) if (boardState.getField(i) == FIELD_NONE) countFehlen++;
            for (int i = 0; i < countFehlen; i++) if (boardState.getField(27 - i) == FIELD_NONE) return 1;
            return 3;
        }
        if (fieldState == FIELD_FIGURE3) {
            int countFehlen = 0;
            for (int i = 12; i < 16; i++) if (boardState.getField(i) == FIELD_NONE) countFehlen++;
            for (int i = 0; i < countFehlen; i++) if (boardState.getField(31 - i) == FIELD_NONE) return 1;
            return 3;
        }
        System.out.println("Fehler oder ich bin doof");
        return -1;
    }

    private int figurZurueckAufStartpositionen(int feld) {
        FieldState field = boardState.getField(feld);
        boardState.setField(feld, FIELD_NONE);
        if (field == FIELD_FIGURE0) {
            for (int i = 0; i < 4; i++) {
                if (boardState.getField(i) == FIELD_NONE) {
                    boardState.setField(i, FIELD_FIGURE0);
                    return i;
                }
            }
        }
        if (field == FIELD_FIGURE1) {
            for (int i = 4; i < 8; i++) {
                if (boardState.getField(i) == FIELD_NONE) {
                    boardState.setField(i, FIELD_FIGURE1);
                    return i;
                }
            }
        }
        if (field == FIELD_FIGURE2) {
            for (int i = 8; i < 12; i++) {
                if (boardState.getField(i) == FIELD_NONE) {
                    boardState.setField(i, FIELD_FIGURE2);
                    return i;
                }
            }
        }
        if (field == FIELD_FIGURE3) {
            for (int i = 12; i < 16; i++) {
                if (boardState.getField(i) == FIELD_NONE) {
                    boardState.setField(i, FIELD_FIGURE3);
                    return i;
                }
            }
        }
        System.out.println("Fehler oder ich bin doof");
        return -1;
    }

    @Override
    public synchronized void leaveGame(LoggedInInterface lii) throws RemoteException {
        removePlayerIntern(lii);
    }

    private synchronized void removePlayerIntern(LoggedInInterface lii) {
        int i = isInGame(lii);
        if (i == -1) return;
        clients[i] = null;
        clientsUpdate[i] = null;
        names[i] = "Bot" + i;
        System.out.println("removed player " + i);
        for (int j = 0; j < spielerAnzahl; j++) {
            if (clients[j] != null) break;
            if (j == spielerAnzahl - 1) {
                aktiverSpieler = -1;
                break;
            }
        }
        if (aktiverSpieler == i) doBotMove(aktiverSpieler);
    }

    @SuppressWarnings("BusyWait")
    private void doBotMove(int spieler) {
        new Thread(() -> {
            while (anzahlWuerfeln > 0 && spieler == aktiverSpieler) {
                if (zahlGewuerfelt < 1) { // schon gewürfelt, falls Spieler ersetzt / nicht gezogen
                    try {
                        sleep(BOT_WAIT_WUERFELN);
                    } catch (InterruptedException e) {
                        System.err.println("schlafen unterbrochen");
                    }
                    throwDiceIntern();
                }
                if (zahlGewuerfelt == -1) break;
                int[] move = getValidMove(boardState, fields[aktiverSpieler], zahlGewuerfelt);
                //System.out.println("move: " + move[0] + " -> " + move[1]);
                if (move[0] != -1) {
                    try {
                        sleep(BOT_WAIT_ZIEHEN);
                    } catch (InterruptedException e) {
                        System.err.println("schlafen unterbrochen");
                    }
                    int ret = submitMoveIntern(move[0], move[1]);
                    if (ret == 1 && spieler != aktiverSpieler) break;
                } else {
                    if (anzahlWuerfeln == 0) {
                        System.out.println("--------------------------bot break-------------------------------");
                        break;
                    }
                }
                zahlGewuerfelt = -1;
            }
        }).start();
    }

    @Override
    public GameStatistics getStatistics() throws RemoteException {
        return gameStatistics;
    }

    private void displayDiceAll(int wurf) {
        for (int i = 0; i < spielerAnzahl; i++) {
            if (clients[i] != null) displayDice(clients[i], clientsUpdate[i], wurf);
        }
    }

    private void displayDice(LoggedInInterface lii, UpdateGameInterface ugi, int wurf) {
        new Thread(() -> {
            try {
                ugi.displayDice(wurf);
            } catch (RemoteException e) {
                removePlayerIntern(lii);
            }
        }).start();
    }

    private void displayNewStateAll(BoardState state, int[] changed, String[] names, int turn) {
        for (int i = 0; i < spielerAnzahl; i++) {
            if (clients[i] != null) displayNewStateIntern(clients[i], clientsUpdate[i], state, changed, names, turn);
        }
    }

    private void displayNewStateIntern(LoggedInInterface lii, UpdateGameInterface ugi, BoardState state, int[] changed, String[] names, int turn) {
        new Thread(() -> {
            try {
                ugi.displayNewState((state == null ? null : BoardState.copyOf(state)), changed, names, turn);
            } catch (RemoteException e) {
                removePlayerIntern(lii);
            }
        }).start();
    }

    private int isInGame(LoggedInInterface lii) {
        for (int i = 0; i < spielerAnzahl; i++) {
            if (clients[i] == lii) return i;
        }
        return -1;
    }

    private void sendYourTurn(int spieler) {
        new Thread(() -> {
            try {
                clientsUpdate[spieler].yourTurn();
            } catch (RemoteException e) {
                removePlayerIntern(clients[spieler]);
            }
        }).start();
    }

    private class StartGame extends TimerTask {
        @Override
        public void run() {
            aktiverSpieler = spielerAnzahl - 1;
            nextPlayer(new int[]{});
        }
    }


    // todo entfernen
    private ServerBoardStateDisplay display;

    private class UpdateDisplay extends TimerTask {
        @Override
        public void run() {
            String s = ServerBoardStateDisplay.getText(gameStatistics, clients, names, fields, finished, anzahlFinished, spielerAnzahl, aktiverSpieler, zahlGewuerfelt, anzahlWuerfeln);
            display.drawBoard(boardState, s);
        }
    }

    private void boardWriteInit() {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fBoard));
             FileWriter fw = new FileWriter(fText)) {
            os.writeObject(boardState);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void boardWrite(String s) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fBoard, true));
             FileWriter fw = new FileWriter(fText, true)) {
            os.writeObject(boardState);
            fw.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
