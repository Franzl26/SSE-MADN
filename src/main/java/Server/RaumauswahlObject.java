package Server;

import DataAndMethods.Room;
import DataAndMethods.Rooms;
import RMIInterfaces.LobbyInterface;
import RMIInterfaces.RaumauswahlInterface;
import RMIInterfaces.UpdateLobbyInterface;
import RMIInterfaces.UpdateRoomsInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class RaumauswahlObject extends UnicastRemoteObject implements RaumauswahlInterface {
    private final HashMap<UpdateRoomsInterface, String> clients = new HashMap<>();
    private final Rooms rooms;
    private final HashMap<Room, LobbyObject> roomLobbyMap;

    protected RaumauswahlObject() throws RemoteException {
        rooms = new Rooms();
        roomLobbyMap = new HashMap<>();
    }

    public void addClient(UpdateRoomsInterface uri, String username) throws RemoteException {
        synchronized (clients) {
            clients.put(uri, username);
        }
    }

    @Override
    public void subscribeToRoomUpdates(UpdateRoomsInterface uri, String username) {
        synchronized (clients) {
            clients.put(uri,username);
        }
    }


    @Override
    public void unsubscribeFromRoomUpdates(UpdateRoomsInterface uri) throws RemoteException {
        unsubscribeFromRoomUpdatesPrivate(uri);
    }

    @Override
    public synchronized LobbyInterface createNewRoom(UpdateRoomsInterface uri, UpdateLobbyInterface uli) throws RemoteException {
        if (!clients.containsKey(uri)) return null;
        if (rooms.maxRoomsReached()) return null;
        Room newRoom = new Room();
        newRoom.addPlayer(clients.get(uri));
        rooms.addRoom(newRoom);
        LobbyObject lobbyObj = new LobbyObject(this, newRoom);
        lobbyObj.addUser(clients.get(uri), uli);
        roomLobbyMap.put(newRoom, lobbyObj);
        unsubscribeFromRoomUpdatesPrivate(uri);

        return lobbyObj;
    }

    @Override
    public synchronized LobbyInterface enterRoom(UpdateRoomsInterface uri, Room room, UpdateLobbyInterface uli) throws RemoteException {
        if (!clients.containsKey(uri)) return null;
        if (room.getCount() == 4) return null;
        room.addPlayer(clients.get(uri));
        LobbyObject lobbyObj = roomLobbyMap.get(room);
        lobbyObj.addUser(clients.get(uri), uli);
        unsubscribeFromRoomUpdatesPrivate(uri);
        return lobbyObj;
    }

    protected synchronized void removeRoom(Room room) {
        roomLobbyMap.remove(room);
    }

    private void unsubscribeFromRoomUpdatesPrivate(UpdateRoomsInterface uri) {
        synchronized (clients) {
            clients.remove(uri);
        }
    }

    protected void updateAllRooms() {
        Set<UpdateRoomsInterface> copy;
        synchronized (clients) {
            copy =  clients.keySet();
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
