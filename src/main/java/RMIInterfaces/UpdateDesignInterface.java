package RMIInterfaces;

import DataAndMethods.BoardConfiguration;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateDesignInterface extends Remote {
    void updateDesign(BoardConfiguration config) throws RemoteException;
}
