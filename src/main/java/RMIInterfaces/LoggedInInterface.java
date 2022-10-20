package RMIInterfaces;

import DataAndMethods.BoardConfigurationBytes;
import DataAndMethods.Room;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoggedInInterface extends Remote {
    void logOut() throws RemoteException;

    String getUsername() throws RemoteException;

    void subscribeToRoomUpdates(UpdateRoomsInterface uri) throws RemoteException;

    void unsubscribeFromRoomUpdates() throws RemoteException;

    /**
     * @return -1 fehlgeschlagen, 1 erfolgreich
     */
    int createNewRoom(UpdateLobbyInterface uli) throws RemoteException;

    /**
     * @return -1 fehlgeschlagen, 1 erfolgreich
     */
    int enterRoom(Room room, UpdateLobbyInterface uli) throws RemoteException;

    /**
     * @return -1 schon 4 Bot vorhanden, -2 nicht in Lobby, 1 erfolgreich todo
     */
    int addBot() throws RemoteException;

    /**
     * @return -1 kein Bot mehr da, -2 nicht in Lobby, 1 erfolgreich todo
     */
    int removeBot() throws RemoteException;

    /**
     * @return -1 nicht genug Spieler, -2 nicht in Lobby, 1 erfolgreich
     */
    int spielStarten(UpdateGameInterface ugi) throws RemoteException;

    void raumVerlassen() throws RemoteException;

    String[] getDesignsList() throws RemoteException;

    void designBestaetigen(String design) throws RemoteException;

    BoardConfigurationBytes getBoardConfig(String design) throws RemoteException;
}
