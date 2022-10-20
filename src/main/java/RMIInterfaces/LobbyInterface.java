package RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyInterface extends Remote {
    /**
     * @return -1 schon 4 Bot vorhanden, -2 nicht in Lobby, 1 erfolgreich todo
     */
    int addBot(LoggedInInterface lii) throws RemoteException;

    /**
     * @return -1 kein Bot mehr da, -2 nicht in Lobby, 1 erfolgreich todo
     */
    int removeBot(LoggedInInterface lii) throws RemoteException;

    /**
     * @return -1 nicht genug Spieler, -2 nicht in Lobby, 1 erfolgreich
     */
    int spielStarten(LoggedInInterface lii, UpdateGameInterface ugi) throws RemoteException;

    void designBestaetigen(LoggedInInterface lii, String design) throws RemoteException;

    void raumVerlassen(LoggedInInterface lii) throws RemoteException;
}
