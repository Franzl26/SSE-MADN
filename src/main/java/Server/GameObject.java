package Server;

import DataAndMethods.GameStatistics;
import RMIInterfaces.GameInterface;
import RMIInterfaces.UpdateGameInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GameObject extends UnicastRemoteObject implements GameInterface {
    protected GameObject() throws RemoteException {
    }

    @Override
    public boolean submitMove(UpdateGameInterface ugi, int from, int to) throws RemoteException {
        return false;
    }

    @Override
    public int throwDice(UpdateGameInterface ugi) throws RemoteException {
        return 0;
    }

    @Override
    public void leaveGame(UpdateGameInterface ugi) throws RemoteException {

    }

    @Override
    public GameStatistics getStatistics(UpdateGameInterface ugi) throws RemoteException {
        return null;
    }
}
