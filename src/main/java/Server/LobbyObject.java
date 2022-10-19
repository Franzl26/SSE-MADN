package Server;

import DataAndMethods.Room;
import RMIInterfaces.LobbyInterface;
import RMIInterfaces.UpdateLobbyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class LobbyObject extends UnicastRemoteObject implements LobbyInterface {
    private final String[] names = new String[4];
    private final UpdateLobbyInterface[] clients = new UpdateLobbyInterface[4];
    private int bots = 0;
    private int spieler = 0;
    private final RaumauswahlObject raumauswahl;
    private final Room room;

    protected LobbyObject(RaumauswahlObject raumauswahlObject, Room room) throws RemoteException {
        raumauswahl = raumauswahlObject;
        this.room = room;
    }

    public synchronized void addUser(String username, UpdateLobbyInterface uli) throws RemoteException {
        if (checkInLobby(uli)) return;
        clients[bots + spieler] = uli;
        names[bots + spieler] = username;
        spieler++;
        update();
    }

    @Override
    public synchronized int addBot(UpdateLobbyInterface uli) throws RemoteException {
        if (bots + spieler == 4) return -1;
        names[bots + spieler] = "Bot" + (bots + 1);
        bots++;
        System.out.println(Arrays.toString(names));
        update();
        return 1;
    }

    @Override
    public synchronized int removeBot(UpdateLobbyInterface uli) throws RemoteException {
        if (bots == 0) return -1;
        for (int i = 3; i >= 0; i--) {
            if (names[i] != null && names[i].startsWith("Bot")) {
                removePlayer(i);
                break;
            }
        }
        bots--;
        System.out.println(Arrays.toString(names));
        update();
        return 1;
    }

    @Override
    public synchronized int spielStarten(UpdateLobbyInterface uli) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int designAnpassen(UpdateLobbyInterface uli) throws RemoteException {
        return 0;
    }

    @Override
    public synchronized void raumVerlassen(UpdateLobbyInterface uli) throws RemoteException {
        if (!checkInLobby(uli)) return;
        raumVerlassenPrivate(uli);
    }

    private void removePlayer(int i) {
        if (i != 3) {
            System.arraycopy(names, i + 1, names, i, 3 - i);
            System.arraycopy(clients, i + 1, clients, i, 3 - i);
        }
        names[3] = null;
        clients[3] = null;
    }

    private boolean checkInLobby(UpdateLobbyInterface uli) {
        for (int i = 0; i < 4; i++) {
            if (uli.equals(clients[i])) return true;
        }
        return false;
    }

    private synchronized void raumVerlassenPrivate(UpdateLobbyInterface uli) {
        for (int i = 3; i >= 0; i--) {
            if (uli.equals(clients[i])) {
                removePlayer(i);
                spieler--;
            }
        }
        if (spieler == 0) {
            raumauswahl.removeRoom(room);
        }
        update();
    }

    private void update() {
        updateAllClients();
        raumauswahl.updateAllRooms();
    }

    private void updateAllClients() {
        UpdateLobbyInterface[] uliCopy;
        String[] namesCopy;
        synchronized (clients) {
            uliCopy = clients.clone();
            namesCopy = names.clone();
        }
        if (uliCopy[0] != null) new Thread(() -> {
            try {
                uliCopy[0].updateNames(namesCopy);
            } catch (RemoteException e) {
                raumVerlassenPrivate(uliCopy[0]);
            }
        }).start();
        if (uliCopy[1] != null) new Thread(() -> {
            try {
                uliCopy[1].updateNames(namesCopy);
            } catch (RemoteException e) {
                raumVerlassenPrivate(uliCopy[1]);
            }
        }).start();
        if (uliCopy[2] != null) new Thread(() -> {
            try {
                uliCopy[2].updateNames(namesCopy);
            } catch (RemoteException e) {
                raumVerlassenPrivate(uliCopy[2]);
            }
        }).start();
        if (uliCopy[3] != null) new Thread(() -> {
            try {
                uliCopy[3].updateNames(namesCopy);
            } catch (RemoteException e) {
                raumVerlassenPrivate(uliCopy[3]);
            }
        }).start();
    }
}
