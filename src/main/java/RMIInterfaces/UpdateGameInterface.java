package RMIInterfaces;

import DataAndMethods.BoardState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateGameInterface extends Remote {
    void welcomeToGame(GameInterface game) throws RemoteException;

    void displayNewState(BoardState state, int[] changed, String[] names, int turn) throws RemoteException;

    //void yourTurn() throws RemoteException;

    void displayDice(int number) throws RemoteException;

    void rollDiceOver() throws RemoteException;

    void timesRunning() throws RemoteException;

    void moveFigureOver() throws RemoteException;
}
