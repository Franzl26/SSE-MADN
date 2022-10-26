package server;

import dataAndMethods.Room;
import rmiInterfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyObject extends UnicastRemoteObject implements LobbyInterface {
    private final UpdateLobbyInterface[] clientsUpdate = new UpdateLobbyInterface[4];
    private final LoggedInInterface[] clients = new LoggedInInterface[4];
    private int bots = 0;
    private int spieler = 0;
    private final RaumauswahlObject raumauswahl;
    private final Room room;
    private String boardDesign = "Standard";
    private GameObject gameObject;

    protected LobbyObject(RaumauswahlObject raumauswahlObject, Room room) throws RemoteException {
        raumauswahl = raumauswahlObject;
        this.room = room;
    }

    public synchronized void addUser(LoggedInInterface lii, UpdateLobbyInterface uli) throws RemoteException {
        if (checkInLobby(lii)) return;
        if (gameObject != null) return;
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
        room.addPlayer("Bot" + (spieler + bots));
        update();
        return 1;
    }

    @Override
    public synchronized int removeBot(LoggedInInterface lii) throws RemoteException {
        if (bots == 0) return -1;
        if (!checkInLobby(lii)) return -2;
        for (int i = (bots + spieler - 1); i >= 0; i--) {
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
    public synchronized int spielStartenAnfragen(LoggedInInterface lii) throws RemoteException {
        if ((bots + spieler) < 2) return -1;
        if (!checkInLobby(lii)) return -1;
        gameObject = new GameObject(clients, spieler + bots);
        Timer timer = new Timer("delete Lobby");
        raumauswahl.removeRoom(room);
        timer.schedule(new DeleteLobby(), 5000);
        startGameForAll();
        return 1;
    }

    @Override
    public synchronized GameInterface spielStartet(LoggedInInterface lii, UpdateGameInterface ugi) throws RemoteException {
        if (!checkInLobby(lii)) return null;
        gameObject.checkPlayerIn(lii, ugi);
        raumVerlassenPrivate(lii);
        return gameObject;
    }

    @Override
    public synchronized void designBestaetigen(LoggedInInterface lii, String design) throws RemoteException {
        if (!checkInLobby(lii)) return;
        boardDesign = design;
        System.out.println("neues design gesetzt: " + design);
        sendDesignToEveryone();
    }

    @Override
    public synchronized void raumVerlassen(LoggedInInterface lii) throws RemoteException {
        if (!checkInLobby(lii)) return;
        raumVerlassenPrivate(lii);
    }

    private synchronized void raumVerlassenPrivate(LoggedInInterface lii) {
        for (int i = 3; i >= 0; i--) {
            if (lii.equals(clients[i])) {
                removePlayer(i);
                spieler--;
            }
        }
        if (spieler < 1) {
            raumauswahl.removeRoom(room);
        }
        update();
    }

    private synchronized boolean checkInLobby(LoggedInInterface lii) {
        for (int i = 0; i < 4; i++) {
            if (lii.equals(clients[i])) return true;
        }
        return false;
    }

    private synchronized void removePlayer(int i) {
        if (i != 3) {
            System.arraycopy(clientsUpdate, i + 1, clientsUpdate, i, 3 - i);
            System.arraycopy(clients, i + 1, clients, i, 3 - i);
        }
        room.removePlayer(i);
        clientsUpdate[3] = null;
        clients[3] = null;
    }

    private synchronized void update() {
        updateAllClients();
        raumauswahl.updateAllRooms();
    }

    private synchronized void sendDesignToEveryone() {
        for (int i = 0; i < spieler + bots; i++) {
            if (clientsUpdate[i] != null) sendDesignToClient(i);
        }
    }

    private synchronized void sendDesignToClient(int client) {
        new Thread(() -> {
            try {
                clientsUpdate[client].updateDesign(boardDesign);
            } catch (RemoteException e) {
                raumVerlassenPrivate(clients[client]);
            }
        }).start();
    }

    private synchronized void startGameForAll() {
        for (int i=0;i<bots+spieler;i++) {
            if (clientsUpdate[i] != null) startGameClient(i);
        }
    }

    private synchronized void startGameClient(int client) {
        new Thread(() -> {
            try {
                clientsUpdate[client].gameStarts();
            } catch (RemoteException e) {
                raumVerlassenPrivate(clients[client]);
            }
        }).start();
    }

    private synchronized void updateAllClients() {
        String[] names = new String[4];
        try {
            for (int i = 0; i < (spieler + bots); i++) {
                if (clients[i] == null) names[i] = "Bot" + (i + 1);
                else names[i] = clients[i].getUsername();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < spieler + bots; i++) {
            if (clientsUpdate[i] != null) updateClient(names, i);
        }
    }

    private synchronized void updateClient(String[] names, int client) {
        new Thread(() -> {
            try {
                clientsUpdate[client].updateNames(names);
            } catch (RemoteException e) {
                raumVerlassenPrivate(clients[client]);
            }
        }).start();
    }

    private class DeleteLobby extends TimerTask {

        @Override
        public void run() {
            gameObject = null;
            for (int i = 1; i < 4; i++) {
                if (clients[i] != null) raumVerlassenPrivate(clients[i]);
            }
        }
    }
}
