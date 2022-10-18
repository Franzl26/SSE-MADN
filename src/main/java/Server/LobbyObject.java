package Server;

import DataAndMethods.Room;
import RMIInterfaces.LobbyInterface;
import RMIInterfaces.UpdateLobbyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LobbyObject extends UnicastRemoteObject implements LobbyInterface {
    protected LobbyObject(Room room) throws RemoteException {
    }

    public void addUser(String username, UpdateLobbyInterface uli) throws RemoteException {
        // todo
    }
}
