package rmiInterfaces;

import dataAndMethods.BoardState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateGameInterface extends Remote {
    /**
     * @param state   null: nichts neu zeichnen
     * @param changed null: alles neu zeichnen, sonst Array der neu zu zeichnenden Felder
     * @param names   Array der Namen
     * @param turn    = -1 wenn keiner sonst 0-3
     */
    void displayNewState(BoardState state, int[] changed, String[] names, int turn) throws RemoteException;

    void displayDice(int number) throws RemoteException;

    void rollDiceOver() throws RemoteException;

    void timesRunning() throws RemoteException;

    void moveFigureOver() throws RemoteException;
}
