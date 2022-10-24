package rmiInterfaces;

import dataAndMethods.Rooms;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateRoomsInterface extends Remote {
    void updateRooms(Rooms rooms) throws RemoteException;
}
