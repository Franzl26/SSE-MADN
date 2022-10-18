package RMIInterfaces;

import DataAndMethods.Room;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RaumauswahlInterface extends Remote {

    void unsubscribeFromRoomUpdates(UpdateRoomsInterface rooms) throws RemoteException;

    LobbyInterface createNewRoom(String username, UpdateLobbyInterface uli) throws RemoteException;

    LobbyInterface enterRoom(String username, Room room, UpdateLobbyInterface uli) throws RemoteException;

}
