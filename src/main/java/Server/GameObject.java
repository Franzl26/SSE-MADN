package Server;

import DataAndMethods.BoardState;
import DataAndMethods.FieldState;
import DataAndMethods.GameStatistics;
import DataAndMethods.Player;
import RMIInterfaces.GameInterface;
import RMIInterfaces.LoggedInInterface;
import RMIInterfaces.UpdateGameInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import static DataAndMethods.FieldState.*;

public class GameObject extends UnicastRemoteObject implements GameInterface {
    private final GameStatistics gameStatistics = new GameStatistics();

    private final LoggedInInterface[] clients;
    private final UpdateGameInterface[] clientsUpdate;
    private final String[] names;
    private final FieldState[] fields;

    //private final HashMap<LoggedInInterface, UpdateGameInterface> clients = new HashMap<>(4, 1);
    //private final Player[] spieler;
    private final int spielerAnzahl;
    private final String design;
    private final BoardState boardState;
    private int aktiverSpieler = -1;

    protected GameObject(LoggedInInterface[] lii, int spielerUndBotsAnz, String design) throws RemoteException {
        spielerAnzahl = spielerUndBotsAnz;
        this.design = design;
        clients = lii;
        clientsUpdate = new UpdateGameInterface[spielerAnzahl];
        names = new String[spielerAnzahl];
        fields = new FieldState[spielerAnzahl];

        if (spielerAnzahl == 2) {
            names[0] = (clients[0]==null?"Bot0":clients[0].getUsername());
            names[1] = (clients[1]==null?"Bot0":clients[1].getUsername());
            fields[0] = FIELD_FIGURE0;
            fields[1] = FIELD_FIGURE2;
        } else {
            for (int i = 0; i < spielerAnzahl; i++) {
                names[i] = (clients[i] == null ? "Bot" + 1 : clients[i].getUsername());
                fields[i] = switch (i) {
                    case 0 -> FIELD_FIGURE0;
                    case 1 -> FIELD_FIGURE1;
                    case 2 -> FIELD_FIGURE2;
                    default -> FIELD_FIGURE3;
                };
            }
        }
        boardState = new BoardState(spielerAnzahl);
    }

    public synchronized void checkPlayerIn(LoggedInInterface lii, UpdateGameInterface ugi) {
        for (int i = 0; i < spielerAnzahl; i++) {
            if (clients[i] == lii) {
                clientsUpdate[i] = ugi;
                System.out.println("ugi set for player" + i);
                break;
            }
        }
        displayNewStateIntern(ugi, boardState, null, names, -1);
    }

    @Override
    public synchronized boolean submitMove(LoggedInInterface lii, int from, int to) throws RemoteException {
        return false;
    }

    @Override
    public synchronized int throwDice(LoggedInInterface lii) throws RemoteException {
        return 0;
    }

    @Override
    public synchronized void leaveGame(LoggedInInterface lii) throws RemoteException {
        for (int i = 0; i < spielerAnzahl; i++) {
            if (clients[i] == lii) {
                clients[i] = null;
                clientsUpdate[i] = null;
                names[i] = "Bot" + i;
            }
        }
    }

    @Override
    public GameStatistics getStatistics() throws RemoteException {
        return gameStatistics;
    }

    private void displayNewStateIntern(UpdateGameInterface ugi, BoardState state, int[] changed, String[] names, int turn) {
        new Thread(() -> {
            try {
                ugi.displayNewState(state, changed, names, turn);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
