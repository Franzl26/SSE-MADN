package server;

import app.ServerBoardStateDisplay;
import dataAndMethods.BoardState;
import dataAndMethods.FieldState;
import dataAndMethods.GameStatistics;
import javafx.application.Platform;
import rmiInterfaces.GameInterface;
import rmiInterfaces.LoggedInInterface;
import rmiInterfaces.UpdateGameInterface;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static dataAndMethods.FieldState.*;
import static dataAndMethods.MiscMethods.*;
import static java.lang.Thread.sleep;

public class GameObject extends UnicastRemoteObject implements GameInterface {
    private static final int BOT_WAIT_WUERFELN = 1000;
    private static final int BOT_WAIT_ZIEHEN = 2000;
    private static final int DELAY_WUERFELN = 10000;
    private static final int DELAY_SPIELZUG = 30000;
    private static final int DELAY_WAITING = 5000;

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

    private Timer timerWuerfeln = new Timer();
    private Timer timerZiehen = new Timer();
    private Timer timerWaiting = new Timer();

    // todo entfernen
    private final int[] wuerfel = new int[]{1,2,3,1,3,2,4,6,5};
    private int wuerfelCount = 0;

    private BoardState[] states = new BoardState[25];
    private int boardAnzahl = 0;
    private final File fBoard = new File("./resources/boards");
    private final File fText = new File("./resources/text.txt");

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
        gameStatistics = new GameStatistics();
        gameStatistics.setNames(names);
        boardState = new BoardState(spielerAnzahl);

        //boardState.setCustom(); // todo entfernen
        states[boardAnzahl++] = BoardState.copyOf(boardState);

        Timer t = new Timer("startGame");
        t.schedule(new StartGame(), 3000);


        // todo entfernen
        /*Platform.runLater(() -> {
            display = ServerBoardStateDisplay.ServerBoardStateDisplayStart();
            Timer t2 = new Timer("boardZeichen");
            t2.schedule(new UpdateDisplay(), 0, 500);
        });*/
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
        if (zahlGewuerfelt == -1) return -4;
        if (!checkMoveValid(boardState, field, from, to, zahlGewuerfelt)) return -1;
        int[] prio = checkForPrioMove(boardState, field, zahlGewuerfelt);
        if (prio != null && prio[0] < 16 && from > 15) return -1;
        return submitMoveIntern(from, to);
    }

    /**
     * move muss erlaubt sein, wird nur noch gesetzt + ggf Punish + Spieler informiert
     */
    private synchronized int submitMoveIntern(int from, int to) {
        timerZiehen.cancel();
        timerWaiting.cancel();
        System.out.println("Submit " + names[aktiverSpieler] + ": " + from + " -> " + to);
        int[] changed = new int[]{from, to, -1, -1, -1}; // from, to, strafe in loch, jmd geschlagen, strafe Figur die weg
        // wenn geschlagen zur??cksetzen
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
        }
        int[] bestrafung = checkBestrafen(fieldStateFrom, from);
        changed[2] = bestrafung[0];
        changed[4] = bestrafung[1];
        if (changed[2] != -1) gameStatistics.incPrioZugIgnoriert(aktiverSpieler);

        boardState.setField(to, fieldStateFrom);
        boardState.setField(from, FIELD_NONE);

        checkFinished(fieldStateFrom);

        // todo entfernen
        states[boardAnzahl++] = BoardState.copyOf(boardState);
        if (boardAnzahl == states.length) states = Arrays.copyOf(states, states.length * 2);
        boardWrite("\nSubmit " + names[aktiverSpieler] + ": " + from + " -> " + to, true);

        displayNewStateAll(boardState, changed, names, aktiverSpieler);
        nextMove(false);
        return (changed[2] == -1 ? 1 : -2);
    }

    private void checkFinished(FieldState fieldStateFrom) {
        // gewonnen Erkennung
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE0 && boardState.getField(16) == FIELD_FIGURE0 && boardState.getField(17) == FIELD_FIGURE0 && boardState.getField(18) == FIELD_FIGURE0 && boardState.getField(19) == FIELD_FIGURE0) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE1 && boardState.getField(20) == FIELD_FIGURE1 && boardState.getField(21) == FIELD_FIGURE1 && boardState.getField(22) == FIELD_FIGURE1 && boardState.getField(23) == FIELD_FIGURE1) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE2 && boardState.getField(24) == FIELD_FIGURE2 && boardState.getField(25) == FIELD_FIGURE2 && boardState.getField(26) == FIELD_FIGURE2 && boardState.getField(27) == FIELD_FIGURE2) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        if (!finished[aktiverSpieler] && fieldStateFrom == FIELD_FIGURE3 && boardState.getField(28) == FIELD_FIGURE3 && boardState.getField(29) == FIELD_FIGURE3 && boardState.getField(30) == FIELD_FIGURE3 && boardState.getField(31) == FIELD_FIGURE3) {
            finished[aktiverSpieler] = true;
            gameStatistics.setFinish(anzahlFinished, names[aktiverSpieler]);
            anzahlFinished++;
        }
        if (anzahlFinished == spielerAnzahl - 1) {
            for (int i = 0; i < spielerAnzahl; i++) {
                if (!finished[i]) {
                    finished[i] = true;
                    gameStatistics.setFinish(anzahlFinished, names[i]);
                }
            }
            anzahlFinished++;
        }
    }

    /**
     * @return [-1, -1] wenn keine Strafe, sonst Felder, die neu gezeichnet werden m??ssen
     */
    private int[] checkBestrafen(FieldState aktuellerSpieler, int from) {
        int[] ret = new int[]{-1, -1};
        // bestrafen
        // abr??cken
        if (from != 32 && aktuellerSpieler == FIELD_FIGURE0 && boardState.getField(32) == FIELD_FIGURE0 && boardState.getField(32 + zahlGewuerfelt) != FIELD_FIGURE0
        && (boardState.getField(0) == FIELD_FIGURE0 || boardState.getField(1) == FIELD_FIGURE0 || boardState.getField(2) == FIELD_FIGURE0 || boardState.getField(3) == FIELD_FIGURE0)) {
            ret[0] = figurZurueckAufStartpositionen(32);
            ret[1] = 32;
        }
        if (from != 42 && aktuellerSpieler == FIELD_FIGURE1 && boardState.getField(42) == FIELD_FIGURE1 && boardState.getField(42 + zahlGewuerfelt) != FIELD_FIGURE1
                && (boardState.getField(4) == FIELD_FIGURE1 || boardState.getField(5) == FIELD_FIGURE1 || boardState.getField(6) == FIELD_FIGURE1 || boardState.getField(7) == FIELD_FIGURE1)) {
            ret[0] = figurZurueckAufStartpositionen(42);
            ret[1] = 42;
        }
        if (from != 52 && aktuellerSpieler == FIELD_FIGURE2 && boardState.getField(52) == FIELD_FIGURE2 && boardState.getField(52 + zahlGewuerfelt) != FIELD_FIGURE2
                && (boardState.getField(8) == FIELD_FIGURE2 || boardState.getField(9) == FIELD_FIGURE2 || boardState.getField(10) == FIELD_FIGURE2 || boardState.getField(11) == FIELD_FIGURE2)) {
            ret[0] = figurZurueckAufStartpositionen(52);
            ret[1] = 52;
        }
        if (from != 62 && aktuellerSpieler == FIELD_FIGURE3 && boardState.getField(62) == FIELD_FIGURE3 && boardState.getField(62 + zahlGewuerfelt) != FIELD_FIGURE3
                && (boardState.getField(12) == FIELD_FIGURE3 || boardState.getField(13) == FIELD_FIGURE3 || boardState.getField(14) == FIELD_FIGURE3 || boardState.getField(15) == FIELD_FIGURE3)) {
            ret[0] = figurZurueckAufStartpositionen(62);
            ret[1] = 62;
        }
        // schlagen
        if (from > 31 && ret[0] != -1) {
            for (int i = 39; i >= 0; i--) {
                int intFieldTo = (i + zahlGewuerfelt) % 40 + 32;
                if ((i + 32) != from && boardState.getField(i + 32) == aktuellerSpieler && boardState.getField(intFieldTo) != aktuellerSpieler && boardState.getField(intFieldTo) != FIELD_NONE) {
                    // nicht ??ber Start beachten
                    if (aktuellerSpieler == FIELD_FIGURE0 && (i + zahlGewuerfelt) >= 40) continue;
                    if (aktuellerSpieler == FIELD_FIGURE1 && (i + zahlGewuerfelt) >= 10 && i < 10) continue;
                    if (aktuellerSpieler == FIELD_FIGURE2 && (i + zahlGewuerfelt) >= 20 && i < 20) continue;
                    if (aktuellerSpieler == FIELD_FIGURE3 && (i + zahlGewuerfelt) >= 30 && i < 30) continue;
                    ret[0] = figurZurueckAufStartpositionen(i + 32);
                    ret[1] = i + 32;
                }
            }
        }
        return ret;
    }

    @Override
    public synchronized int throwDice(LoggedInInterface lii) throws RemoteException {
        int i = isInGame(lii);
        if (i == -1 || i != aktiverSpieler) return -1;
        return throwDiceIntern();
    }

    private synchronized int throwDiceIntern() {
        timerWuerfeln.cancel();
        if (zahlGewuerfelt > 0) {
            System.err.println("Die Pr??fung findet doch statt?");
            if (getValidMove(boardState, fields[aktiverSpieler], zahlGewuerfelt)[0] != -1) return -2;
        }
        if (anzahlWuerfeln == 0) return -1;

        if (wuerfelCount < wuerfel.length) {
            zahlGewuerfelt = wuerfel[wuerfelCount++];   // todo entfernen
        } else {
            zahlGewuerfelt = (int) (Math.random() * 6 + 1);
        }

        gameStatistics.incZahlGewuerfelt(aktiverSpieler, zahlGewuerfelt - 1);
        System.out.println("throw diceIntern: " + names[aktiverSpieler] + " : " + zahlGewuerfelt);
        anzahlWuerfeln--;
        displayDiceAll(zahlGewuerfelt);
        if (zahlGewuerfelt == 6) {
            anzahlWuerfeln = 1;
        }
        boardWrite("\ngew??rfelt: " + names[aktiverSpieler] + " : " + zahlGewuerfelt, false);

        if (getValidMove(boardState, fields[aktiverSpieler], zahlGewuerfelt)[0] == -1) { // kein Zug m??glich
            nextMove(false);
            return -3;
        }
        if (clients[aktiverSpieler] != null) {
            timerZiehen = new Timer("TimerZiehen");
            timerZiehen.schedule(new ZiehenEnde(aktiverSpieler), DELAY_SPIELZUG);
            timerWaiting = new Timer("TimerWaiting");
            timerWaiting.schedule(new Waiting(aktiverSpieler), DELAY_WAITING);
        }
        return 1;
    }

    private void nextMove(boolean naechstenErzwingen) {
        new Thread(()-> {
            zahlGewuerfelt = -1;
            try {
                if (anzahlFinished == spielerAnzahl) {
                    System.out.println("Spiel zu Ende");
                    aktiverSpieler = -1;
                    zahlGewuerfelt = -1;
                    anzahlWuerfeln = -1;
                    Thread.currentThread().interrupt();
                    return;
                }
                if (anzahlWuerfeln < 1 || naechstenErzwingen) {
                    System.out.println("set next");
                    aktiverSpieler = (aktiverSpieler + 1) % spielerAnzahl;
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        System.err.println("schlafen unterbrochen");
                    }
                    while (finished[aktiverSpieler]) aktiverSpieler = (aktiverSpieler + 1) % spielerAnzahl;
                    anzahlWuerfeln = getAnzahlWuerfelnNext(aktiverSpieler);
                    displayNewStateAll(boardState, new int[]{}, names, aktiverSpieler);
                }
                if (clients[aktiverSpieler] == null) {
                    doBotMove(aktiverSpieler, true);
                } else {
                    timerWuerfeln = new Timer("TimerWurf");
                    timerWuerfeln.schedule(new WuerfelnEnde(aktiverSpieler), DELAY_WUERFELN);
                }
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
        System.err.println("Fehler oder ich bin doof");
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
        System.err.println("Fehler oder ich bin doof");
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
                System.err.println("beendet");
                aktiverSpieler = -1;
                break;
            }
        }
        System.out.println("Spiel verlassen: " + lii);
        gameStatistics.setNames(names);
    }

    private void doBotMove(int spieler, boolean sleep) {
        new Thread(() -> {
            if (zahlGewuerfelt < 1 && spieler == aktiverSpieler) { // schon gew??rfelt, falls Spieler ersetzt / nicht gezogen
                try {
                    if (sleep) sleep(BOT_WAIT_WUERFELN);
                } catch (InterruptedException e) {
                    System.err.println("schlafen unterbrochen");
                }
                if (throwDiceIntern() == -3) return; // kein Zug m??glich
            }
            int[] move = getValidMove(boardState, fields[aktiverSpieler], zahlGewuerfelt);
            if (move[0] != -1 && spieler == aktiverSpieler) {
                try {
                    if (sleep) sleep(BOT_WAIT_ZIEHEN);
                } catch (InterruptedException e) {
                    System.err.println("schlafen unterbrochen");
                }
                submitMoveIntern(move[0], move[1]);
            }
        }).start();
    }

    @Override
    public GameStatistics getStatistics() throws RemoteException {
        return GameStatistics.copyOf(gameStatistics);
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
                ugi.displayNewState((state == null ? null : BoardState.copyOf(state)), (changed==null?null:changed.clone()), names.clone(), turn);
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

    private class StartGame extends TimerTask {
        @Override
        public void run() {
            aktiverSpieler = spielerAnzahl - 1;
            nextMove(true);
        }
    }

    private class WuerfelnEnde extends TimerTask {
        private final int spieler;

        public WuerfelnEnde(int spieler) {
            this.spieler = spieler;
        }

        @Override
        public void run() {
            System.out.println("wuerfeln vorbei: " + spieler);
            if (zahlGewuerfelt < 1 && spieler == aktiverSpieler) {
                doBotMove(spieler, false);
                try {
                    clientsUpdate[spieler].rollDiceOver();
                } catch (RemoteException e) {
                    removePlayerIntern(clients[spieler]);
                }
            }
        }
    }

    private class ZiehenEnde extends TimerTask {
        private final int spieler;

        public ZiehenEnde(int spieler) {
            this.spieler = spieler;
        }

        @Override
        public void run() {
            System.out.println("ziehen vorbei: " + spieler);
            if (spieler == aktiverSpieler) {
                doBotMove(spieler, false);
                try {
                    clientsUpdate[spieler].moveFigureOver();
                } catch (RemoteException e) {
                    removePlayerIntern(clients[spieler]);
                }
            }
        }
    }

    private class Waiting extends TimerTask {
        private final int spieler;

        private Waiting(int spieler) {
            this.spieler = spieler;
        }

        @Override
        public void run() {
            System.out.println("waiting vorbei: " + spieler);
            if (spieler == aktiverSpieler) {
                try {
                    clientsUpdate[spieler].timesRunning();
                } catch (RemoteException e) {
                    removePlayerIntern(clients[spieler]);
                }
            }
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
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fBoard)); FileWriter fw = new FileWriter(fText)) {
            os.writeObject(states);
            fw.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void boardWrite(String s, boolean writeBoard) {
        try (FileWriter fw = new FileWriter(fText, true)) {
            fw.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (writeBoard) {
            try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fBoard))) {
                os.writeObject(states);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
