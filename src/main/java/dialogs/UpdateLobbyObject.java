package dialogs;

import rmiInterfaces.UpdateLobbyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UpdateLobbyObject extends UnicastRemoteObject implements UpdateLobbyInterface {
    private final LobbyPane pane;

    protected UpdateLobbyObject(LobbyPane lobbyPane) throws RemoteException {
        pane = lobbyPane;
    }

    @Override
    public void updateNames(String[] names) throws RemoteException {
        pane.drawNames(names);
    }

    @Override
    public void gameStarts() throws RemoteException {
        pane.gameStarts();
    }

    @Override
    public void updateDesign(String design) throws RemoteException {
        pane.setDesign(design);
    }
}
