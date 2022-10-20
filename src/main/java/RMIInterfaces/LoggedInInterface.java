package RMIInterfaces;

import Server.RaumauswahlObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggedInInterface extends Remote {
    void logOut() throws RemoteException;
    RaumauswahlObject getRoomSelect() throws RemoteException;
}
