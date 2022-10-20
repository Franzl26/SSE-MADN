package Server;

import DataAndMethods.GameStatistics;
import RMIInterfaces.GameInterface;
import RMIInterfaces.LoggedInInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameObject extends UnicastRemoteObject implements GameInterface {
    protected GameObject() throws RemoteException {
    }

    @Override
    public boolean submitMove(LoggedInInterface lii, int from, int to) throws RemoteException {
        return false;
    }

    @Override
    public int throwDice(LoggedInInterface lii) throws RemoteException {
        return 0;
    }

    @Override
    public void leaveGame(LoggedInInterface lii) throws RemoteException {

    }

    @Override
    public GameStatistics getStatistics() throws RemoteException {
        return null;
    }
}
