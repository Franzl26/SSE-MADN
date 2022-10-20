package Server;

import DataAndMethods.Room;
import RMIInterfaces.LobbyInterface;
import RMIInterfaces.LoggedInInterface;
import RMIInterfaces.UpdateGameInterface;
import RMIInterfaces.UpdateLobbyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LobbyObject extends UnicastRemoteObject implements LobbyInterface {
    private final UpdateLobbyInterface[] clientsUpdate = new UpdateLobbyInterface[4];
    private final LoggedInInterface[] clients = new LoggedInInterface[4];
    private int bots = 0;
    private int spieler = 0;
    private final RaumauswahlObject raumauswahl;
    private final Room room;
    private String boardDesign = "Standard";

    protected LobbyObject(RaumauswahlObject raumauswahlObject, Room room) throws RemoteException {
        raumauswahl = raumauswahlObject;
        this.room = room;
    }

    public synchronized void addUser(LoggedInInterface lii, UpdateLobbyInterface uli) throws RemoteException {
        if (checkInLobby(lii)) return;
        clients[bots + spieler] = lii;
        clientsUpdate[bots + spieler] = uli;
        spieler++;
        update();
    }

    @Override
    public synchronized int addBot(LoggedInInterface lii) throws RemoteException {
        if (bots + spieler == 4) return -1;
        if (!checkInLobby(lii)) return -2;
        bots++;
        update();
        return 1;
    }

    @Override
    public synchronized int removeBot(LoggedInInterface lii) throws RemoteException {
        if (bots == 0) return -1;
        if (!checkInLobby(lii)) return -2;
        for (int i = (bots + spieler-1); i >= 0; i--) {
            if (clients[i] == null) {
                removePlayer(i);
                break;
            }
        }
        bots--;
        update();
        return 1;
    }

    @Override
    public synchronized int spielStarten(LoggedInInterface lii, UpdateGameInterface ugi) throws RemoteException { // todo
        if (!checkInLobby(lii)) return -2;
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void designBestaetigen(LoggedInInterface lii, String design) throws RemoteException {
        if (!checkInLobby(lii)) return;
        boardDesign = design;
        System.out.println("neues design gesetzt: " + design);
    }

    @Override
    public synchronized void raumVerlassen(LoggedInInterface lii) throws RemoteException {
        if (!checkInLobby(lii)) return;
        raumVerlassenPrivate(lii);
    }

    private boolean checkInLobby(LoggedInInterface lii) {
        for (int i = 0; i < 4; i++) {
            if (lii.equals(clients[i])) return true;
        }
        return false;
    }

    private synchronized void raumVerlassenPrivate(LoggedInInterface lii) {
        for (int i = 3; i >= 0; i--) {
            if (checkInLobby(lii)) {
                removePlayer(i);
                spieler--;
            }
        }
        if (spieler == 0) {
            raumauswahl.removeRoom(room);
        }
        update();
    }

    private void removePlayer(int i) {
        if (i != 3) {
            System.arraycopy(clientsUpdate, i + 1, clientsUpdate, i, 3 - i);
            System.arraycopy(clients, i + 1, clients, i, 3 - i);
        }
        clientsUpdate[3] = null;
        clients[3] = null;
    }

    private void update() {
        updateAllClients();
        raumauswahl.updateAllRooms();
    }

    private void updateAllClients() {
        UpdateLobbyInterface[] uliCopy;
        LoggedInInterface[] liiCopy;
        String[] names = new String[4];
        synchronized (clientsUpdate) {
            uliCopy = clientsUpdate.clone();
            liiCopy = clients.clone();
        }
        try {
            for (int i = 0; i < (spieler+bots); i++) {
                if (liiCopy[i] == null) names[i] = "Bot" + (i + 1);
                else names[i] = liiCopy[i].getUsername();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        if (uliCopy[0] != null) new Thread(() -> {
            try {
                uliCopy[0].updateNames(names);
            } catch (RemoteException e) {
                raumVerlassenPrivate(clients[0]);
            }
        }).start();
        if (uliCopy[1] != null) new Thread(() -> {
            try {
                uliCopy[1].updateNames(names);
            } catch (RemoteException e) {
                raumVerlassenPrivate(clients[1]);
            }
        }).start();
        if (uliCopy[2] != null) new Thread(() -> {
            try {
                uliCopy[2].updateNames(names);
            } catch (RemoteException e) {
                raumVerlassenPrivate(clients[2]);
            }
        }).start();
        if (uliCopy[3] != null) new Thread(() -> {
            try {
                uliCopy[3].updateNames(names);
            } catch (RemoteException e) {
                raumVerlassenPrivate(clients[3]);
            }
        }).start();
    }
}
