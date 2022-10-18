package Dialogs;

import DataAndMethods.Room;
import Dialogs.LobbyPane;
import RMIInterfaces.UpdateLobbyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UpdateLobbyObject extends UnicastRemoteObject implements UpdateLobbyInterface {
    private final LobbyPane pane;

    protected UpdateLobbyObject(LobbyPane lobbyPane) throws RemoteException {
        pane = lobbyPane;
    }

    @Override
    public void updateNames(Room room) throws RemoteException {
        pane.drawNames(room);
    }
}
