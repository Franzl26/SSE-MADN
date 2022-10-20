package RMIInterfaces;

import DataAndMethods.GameStatistics;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {
    boolean submitMove(UpdateGameInterface ugi, int from, int to) throws RemoteException;
    int throwDice(UpdateGameInterface ugi) throws RemoteException;
    void leaveGame(UpdateGameInterface ugi) throws RemoteException;
    GameStatistics getStatistics(UpdateGameInterface ugi) throws RemoteException;
}
