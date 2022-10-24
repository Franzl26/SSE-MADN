package rmiInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyInterface extends Remote {
    /**
     * @return -1 schon 4 Bot vorhanden, 1 erfolgreich
     */
    int addBot(LoggedInInterface lii) throws RemoteException;

    /**
     * @return -1 kein Bot mehr da, 1 erfolgreich
     */
    int removeBot(LoggedInInterface lii) throws RemoteException;

    /**
     * @return -1 Spiel kann nicht gestartet werden, 1 Spiel wird gestartet
     */
    int spielStartenAnfragen(LoggedInInterface lii) throws RemoteException;

    GameInterface spielStartet(LoggedInInterface lii, UpdateGameInterface ugi) throws RemoteException;

    void designBestaetigen(LoggedInInterface lii, String design) throws RemoteException;

    void raumVerlassen(LoggedInInterface lii) throws RemoteException;
}
