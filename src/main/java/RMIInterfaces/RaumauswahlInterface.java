package RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RaumauswahlInterface extends Remote {
    void subscribeToRoomUpdates(UpdateRoomsInterface rooms) throws RemoteException;

    void unsubscribeFromRoomUpdates(UpdateRoomsInterface rooms) throws RemoteException;

    void createNewRoom(UpdateLobbyInterface lobby) throws RemoteException;

}
