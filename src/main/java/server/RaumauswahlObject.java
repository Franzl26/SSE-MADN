package server;

import dataAndMethods.BoardConfigurationBytes;
import dataAndMethods.Room;
import dataAndMethods.Rooms;
import rmiInterfaces.*;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RaumauswahlObject extends UnicastRemoteObject implements RaumauswahlInterface {
    private final HashMap<LoggedInInterface, UpdateRoomsInterface> clients = new HashMap<>();
    private final Rooms rooms;
    private final HashMap<Room, LobbyObject> roomLobbyMap;

    protected RaumauswahlObject() throws RemoteException {
        rooms = new Rooms();
        roomLobbyMap = new HashMap<>();
    }

    public void addClient(LoggedInInterface lii, UpdateRoomsInterface uri) throws RemoteException {
        synchronized (clients) {
            clients.put(lii, uri);
        }
    }

    @Override
    public void subscribeToRoomUpdates(LoggedInInterface lii, UpdateRoomsInterface uri) {
        synchronized (clients) {
            clients.put(lii, uri);
        }
    }


    @Override
    public void unsubscribeFromRoomUpdates(LoggedInInterface lii) throws RemoteException {
        unsubscribeFromRoomUpdatesPrivate(lii);
    }

    @Override
    public synchronized LobbyInterface createNewRoom(LoggedInInterface lii, UpdateLobbyInterface uli) throws RemoteException {
        if (!clients.containsKey(lii)) return null;
        if (rooms.maxRoomsReached()) return null;
        Room newRoom = new Room();
        newRoom.addPlayer(lii.getUsername());
        synchronized (rooms) {
            rooms.addRoom(newRoom);
        }
        LobbyObject lobbyObj = new LobbyObject(this, newRoom);
        lobbyObj.addUser(lii, uli);
        synchronized (roomLobbyMap) {
            roomLobbyMap.put(newRoom, lobbyObj);
        }
        unsubscribeFromRoomUpdatesPrivate(lii);
        return lobbyObj;
    }

    @Override
    public synchronized LobbyInterface enterRoom(LoggedInInterface lii, Room room, UpdateLobbyInterface uli) throws RemoteException {
        if (!clients.containsKey(lii)) return null;
        if (room.getCount() == 4) return null;
        room.addPlayer(lii.getUsername());
        LobbyObject lobbyObj = roomLobbyMap.get(room);
        lobbyObj.addUser(lii, uli);
        unsubscribeFromRoomUpdatesPrivate(lii);
        return lobbyObj;
    }

    @Override
    public BoardConfigurationBytes getBoardConfig(String design) throws RemoteException {
        try {
            return BoardConfigurationBytes.loadBoardKonfiguration("./resources/Server/designs/" + design + "/");
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    @Override
    public String[] getDesignsList() throws RemoteException {
        File f = new File("./resources/Server/designs/");
        return f.list();
    }

    protected void removeRoom(Room room) {
        synchronized (roomLobbyMap) {
            roomLobbyMap.remove(room);
        }
        synchronized (rooms) {
            rooms.removeRoom(room);
        }
    }

    private void unsubscribeFromRoomUpdatesPrivate(LoggedInInterface lii) {
        synchronized (clients) {
            clients.remove(lii);
        }
    }

    protected void updateAllRooms() {
        HashMap<LoggedInInterface, UpdateRoomsInterface> copy;
        synchronized (clients) {
            //noinspection unchecked
            copy = (HashMap<LoggedInInterface, UpdateRoomsInterface>) clients.clone();
        }
        Rooms copyRooms = Rooms.copyOf(rooms);
        copy.keySet().forEach(client -> new Thread(() -> {
            try {
                copy.get(client).updateRooms(copyRooms);
            } catch (RemoteException e) {
                unsubscribeFromRoomUpdatesPrivate(client);
            }
        }).start());
    }
}
