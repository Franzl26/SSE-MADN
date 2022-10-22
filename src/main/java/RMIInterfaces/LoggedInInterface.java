package RMIInterfaces;

import DataAndMethods.BoardConfigurationBytes;
import DataAndMethods.GameStatistics;
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
     * @return -1 schon 4 Bot vorhanden, 1 erfolgreich
     */
    int addBot() throws RemoteException;

    /**
     * @return -1 kein Bot mehr da, 1 erfolgreich
     */
    int removeBot() throws RemoteException;

    /**
     * @return -1 nicht genug Spieler, 1 erfolgreich
     */
    int spielStartenAnfragen() throws RemoteException;

    /**
     * @return -1 Spiel wurde noch nicht gestartet, 1 Spiel wird gestartet
     */
    int spielStartet(UpdateGameInterface ugi) throws RemoteException;

    void raumVerlassen() throws RemoteException;

    String[] getDesignsList() throws RemoteException;

    void designBestaetigen(String design) throws RemoteException;

    BoardConfigurationBytes getBoardConfigBytes(String design) throws RemoteException;

    /**
     * @return -1 Zug nicht möglich nochmal setzten, -2 Prio nicht eingehalten Figur geschlagen, -3 nicht dran
     * 1 Zug durchgeführt
     */
    int submitMove(int from, int to) throws RemoteException;

    /**
     * @return -1 nicht dran, -2 schon gewürfelt, nicht gezogen, 1-6 Zahl
     */
    int throwDice() throws RemoteException;

    void leaveGame() throws RemoteException;

    GameStatistics getStatistics() throws RemoteException;
}
