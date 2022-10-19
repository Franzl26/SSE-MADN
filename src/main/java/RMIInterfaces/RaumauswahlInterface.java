package RMIInterfaces;

import DataAndMethods.Room;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RaumauswahlInterface extends Remote {
    void subscribeToRoomUpdates(UpdateRoomsInterface uri, String username) throws RemoteException;
    void unsubscribeFromRoomUpdates(UpdateRoomsInterface uri) throws RemoteException;

    LobbyInterface createNewRoom(UpdateRoomsInterface uri, UpdateLobbyInterface uli) throws RemoteException;

    LobbyInterface enterRoom(UpdateRoomsInterface uri, Room room, UpdateLobbyInterface uli) throws RemoteException;

}
