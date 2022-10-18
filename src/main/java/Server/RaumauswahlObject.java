package Server;

import ClientLogic.UpdateRoomsObject;
import DataAndMethods.Rooms;
import RMIInterfaces.RaumauswahlInterface;
import RMIInterfaces.UpdateLobbyInterface;
import RMIInterfaces.UpdateRoomsInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RaumauswahlObject extends UnicastRemoteObject implements RaumauswahlInterface {
    private final ArrayList<UpdateRoomsInterface> clients = new ArrayList<>();

    protected RaumauswahlObject() throws RemoteException {
    }

    @Override
    public void subscribeToRoomUpdates(UpdateRoomsInterface room) throws RemoteException {
        synchronized (clients) {
            clients.add(room);
        }
    }

    @Override
    public void unsubscribeFromRoomUpdates(UpdateRoomsInterface room) throws RemoteException {
        unsubscribeFromRoomUpdatesPrivate(room);
    }

    @Override
    public void createNewRoom(UpdateLobbyInterface lobby) throws RemoteException {

    }

    private void unsubscribeFromRoomUpdatesPrivate(UpdateRoomsInterface room) {
        synchronized (clients) {
            clients.remove(room);
        }
    }

    private void updateAllRooms(Rooms rooms) {
        ArrayList<UpdateRoomsInterface> copy;
        synchronized (clients) {
            //noinspection unchecked
            copy = (ArrayList<UpdateRoomsInterface>) clients.clone();
        }
        copy.forEach(client -> {
            new Thread(() -> {
                try {
                    client.updateRooms(rooms);
                } catch (RemoteException e) {
                    unsubscribeFromRoomUpdatesPrivate(client);
                }
            }).start();
        });
    }
}
