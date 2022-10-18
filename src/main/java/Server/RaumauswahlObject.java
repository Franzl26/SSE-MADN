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

public class RaumauswahlObject extends UnicastRemoteObject implements RaumauswahlInterface {
    private final ArrayList<UpdateRoomsInterface> clients = new ArrayList<>();
    private final Rooms rooms;
    private final HashMap<Room, LobbyObject> roomLobbyMap;

    protected RaumauswahlObject() throws RemoteException {
        rooms = new Rooms();
        roomLobbyMap = new HashMap<>();
    }

    public void addClient(UpdateRoomsInterface room) throws RemoteException {
        synchronized (clients) {
            clients.add(room);
        }
    }

    @Override
    public void unsubscribeFromRoomUpdates(UpdateRoomsInterface room) throws RemoteException {
        unsubscribeFromRoomUpdatesPrivate(room);
    }

    @Override
    public LobbyInterface createNewRoom(String username, UpdateLobbyInterface uli) throws RemoteException {
        if (rooms.maxRoomsReached()) return null;
        Room newRoom = new Room();
        newRoom.addPlayer(username);
        rooms.addRoom(newRoom);
        LobbyObject lobbyObj = new LobbyObject(newRoom);
        lobbyObj.addUser(username, uli);
        roomLobbyMap.put(newRoom, lobbyObj);
        return lobbyObj;
    }

    @Override
    public synchronized LobbyInterface enterRoom(String username, Room room, UpdateLobbyInterface uli) throws RemoteException {
         if (room.getCount()==4) return null;
         room.addPlayer(username);
         LobbyObject lobbyObj = roomLobbyMap.get(room);
         lobbyObj.addUser(username, uli);
         return lobbyObj;
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
