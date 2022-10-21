package RMIInterfaces;

import DataAndMethods.BoardState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateGameInterface extends Remote {
    void displayNewState(BoardState state, int[] changed, String[] names, int turn) throws RemoteException;

    void displayDice(int number) throws RemoteException;

    void rollDiceOver(int wurf) throws RemoteException;

    void timesRunning() throws RemoteException;

    void moveFigureOver() throws RemoteException;
}
