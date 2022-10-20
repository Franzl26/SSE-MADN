package RMIInterfaces;

import DataAndMethods.GameStatistics;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {
    boolean submitMove(LoggedInInterface lii, int from, int to) throws RemoteException;

    int throwDice(LoggedInInterface lii) throws RemoteException;

    void leaveGame(LoggedInInterface lii) throws RemoteException;

    GameStatistics getStatistics() throws RemoteException;
}
