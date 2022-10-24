package dialogs;

import dataAndMethods.Rooms;
import rmiInterfaces.UpdateRoomsInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UpdateRoomsObject extends UnicastRemoteObject implements UpdateRoomsInterface {
    private final RoomSelectPane pane;

    protected UpdateRoomsObject(RoomSelectPane roomSelectPane) throws RemoteException {
        pane = roomSelectPane;
    }

    @Override
    public void updateRooms(Rooms rooms) throws RemoteException {
        pane.displayRooms(rooms);
    }
}
