package RMIInterfaces;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardConfigurationBytes;

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
    int spielStarten(UpdateLobbyInterface uli, UpdateGameInterface ugi) throws RemoteException;

    String[] getDesignsList(UpdateLobbyInterface uli) throws RemoteException;

    BoardConfigurationBytes getBoardConfig(UpdateLobbyInterface uli, String design) throws RemoteException;

    void designBestaetigen(UpdateLobbyInterface uli, String design) throws RemoteException;

    void raumVerlassen(UpdateLobbyInterface uli) throws RemoteException;
}
