package rmiInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateLobbyInterface extends Remote {
    void updateNames(String[] names) throws RemoteException;

    void gameStarts() throws RemoteException;

    void updateDesign(String design) throws RemoteException;
}
