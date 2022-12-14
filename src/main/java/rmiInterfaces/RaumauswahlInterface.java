package rmiInterfaces;

import dataAndMethods.BoardConfigurationBytes;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RaumauswahlInterface extends Remote {
    void subscribeToRoomUpdates(LoggedInInterface lii, UpdateRoomsInterface uri) throws RemoteException;

    void unsubscribeFromRoomUpdates(LoggedInInterface lii) throws RemoteException;

    LobbyInterface createNewRoom(LoggedInInterface lii, UpdateLobbyInterface uli) throws RemoteException;

    LobbyInterface enterRoom(LoggedInInterface lii, long roomId, UpdateLobbyInterface uli) throws RemoteException;

    BoardConfigurationBytes getBoardConfig(String design) throws RemoteException;

    String[] getDesignsList() throws RemoteException;
}
