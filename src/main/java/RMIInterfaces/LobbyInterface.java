package RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LobbyInterface extends Remote {
    /**
     * @return -1 schon 4 Bot vorhanden, 1 erfolgreich
     */
    int addBot(UpdateLobbyInterface uli) throws RemoteException;

    /**
     * @return -1 kein Bot mehr da, 1 erfolgreich
     */
    int removeBot(UpdateLobbyInterface uli) throws RemoteException;

    /**
     * @return -1 nicht genug Spieler, 1 erfolgreich
     */
    int spielStarten(UpdateLobbyInterface uli) throws RemoteException;

    int designAnpassen(UpdateLobbyInterface uli) throws RemoteException;

    void raumVerlassen(UpdateLobbyInterface uli) throws RemoteException;
}
