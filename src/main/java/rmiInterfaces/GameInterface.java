package rmiInterfaces;

import dataAndMethods.GameStatistics;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameInterface extends Remote {
    /**
     * @return -1 Zug nicht möglich nochmal setzten, -2 Prio nicht eingehalten Figur geschlagen, -3 nicht dran,
     * -4 noch nicht gewürfelt, 1 Zug durchgeführt
     */
    int submitMove(LoggedInInterface lii, int from, int to) throws RemoteException;

    /**
     * @return -1 nicht dran, -2 schon gewürfelt, 1 erfolgreich gewürfelt
     */
    int throwDice(LoggedInInterface lii) throws RemoteException;

    void leaveGame(LoggedInInterface lii) throws RemoteException;

    GameStatistics getStatistics() throws RemoteException;
}
