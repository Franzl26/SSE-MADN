package RMIInterfaces;

import DataAndMethods.Room;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateLobbyInterface extends Remote {
    void updateNames(Room room) throws RemoteException;
}
