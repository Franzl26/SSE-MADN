package server;

import dataAndMethods.BoardConfigurationBytes;
import dataAndMethods.GameStatistics;
import dataAndMethods.Room;
import rmiInterfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoggedInObject extends UnicastRemoteObject implements LoggedInInterface {
    private final String username;
    private final RaumauswahlInterface raumauswahlObject;
    private final LoginInterface loginInterface;
    private LobbyInterface lobbyInterface;
    private GameInterface gameInterface;

    protected LoggedInObject(String username, RaumauswahlObject roomSelect, LoginInterface loginInterface) throws RemoteException {
        this.username = username;
        raumauswahlObject = roomSelect;
        this.loginInterface = loginInterface;
    }

    @Override
    public void logOut() throws RemoteException {
        loginInterface.logout(this);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void subscribeToRoomUpdates(UpdateRoomsInterface uri) throws RemoteException {
        raumauswahlObject.subscribeToRoomUpdates(this, uri);
    }

    public void unsubscribeFromRoomUpdates() throws RemoteException {
        raumauswahlObject.unsubscribeFromRoomUpdates(this);
    }

    @Override
    public int createNewRoom(UpdateLobbyInterface uli) throws RemoteException {
        lobbyInterface = raumauswahlObject.createNewRoom(this, uli);
        if (lobbyInterface == null) return -1;
        return 1;
    }

    @Override
    public int enterRoom(long roomId, UpdateLobbyInterface uli) throws RemoteException {
        lobbyInterface = raumauswahlObject.enterRoom(this, roomId, uli);
        if (lobbyInterface == null) return -1;
        return 1;
    }

    @Override
    public int addBot() throws RemoteException {
        return lobbyInterface.addBot(this);
    }

    @Override
    public int removeBot() throws RemoteException {
        return lobbyInterface.removeBot(this);
    }

    @Override
    public int spielStartenAnfragen() throws RemoteException {
        return lobbyInterface.spielStartenAnfragen(this);
    }

    @Override
    public int spielStartet(UpdateGameInterface ugi) throws RemoteException {
        gameInterface = lobbyInterface.spielStartet(this, ugi);
        if (gameInterface == null) return -1;
        lobbyInterface = null;
        return 1;
    }

    @Override
    public void raumVerlassen() throws RemoteException {
        lobbyInterface.raumVerlassen(this);
        lobbyInterface = null;
    }

    @Override
    public String[] getDesignsList() throws RemoteException {
        return raumauswahlObject.getDesignsList();
    }

    @Override
    public void designBestaetigen(String design) throws RemoteException {
        lobbyInterface.designBestaetigen(this, design);
    }

    @Override
    public BoardConfigurationBytes getBoardConfigBytes(String design) throws RemoteException {
        return raumauswahlObject.getBoardConfig(design);
    }

    @Override
    public int submitMove(int from, int to) throws RemoteException {
        return gameInterface.submitMove(this, from, to);
    }

    @Override
    public int throwDice() throws RemoteException {
        return gameInterface.throwDice(this);
    }

    @Override
    public void leaveGame() throws RemoteException {
        gameInterface.leaveGame(this);
        gameInterface = null;
    }

    @Override
    public GameStatistics getStatistics() throws RemoteException {
        return gameInterface.getStatistics();
    }

    @Override
    public String toString() {
        return username;
    }
}
