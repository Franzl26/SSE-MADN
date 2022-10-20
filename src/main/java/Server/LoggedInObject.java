package Server;

import RMIInterfaces.LoggedInInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoggedInObject extends UnicastRemoteObject implements LoggedInInterface {
    private final String username;
    protected LoggedInObject(String username) throws RemoteException {
        this.username = username;
    }

    @Override
    public void logOut() throws RemoteException {

    }

    @Override
    public RaumauswahlObject getRoomSelect() throws RemoteException {
        return null;
    }

    public String getUsername() {
        return username;
    }
}
