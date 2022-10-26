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
    private final HashMap<Long, Room> roomIdRoomMap;
    private final HashMap<Long, LobbyObject> roomIdLobbyMap;

    protected RaumauswahlObject() throws RemoteException {
        rooms = new Rooms();
        roomIdLobbyMap = new HashMap<>();
        roomIdRoomMap = new HashMap<>();
    }

    public void addClient(LoggedInInterface lii, UpdateRoomsInterface uri) throws RemoteException {
        if (lii == null || uri == null) return;
        clients.put(lii, uri);
        updateClientRoom(lii);
    }

    @Override
    public void subscribeToRoomUpdates(LoggedInInterface lii, UpdateRoomsInterface uri) {
        if (lii == null || uri == null) return;
        clients.put(lii, uri);
        updateClientRoom(lii);
    }


    @Override
    public void unsubscribeFromRoomUpdates(LoggedInInterface lii) throws RemoteException {
        if (lii == null) return;
        unsubscribeFromRoomUpdatesPrivate(lii);
    }

    @Override
    public synchronized LobbyInterface createNewRoom(LoggedInInterface lii, UpdateLobbyInterface uli) throws RemoteException {
        if (lii == null || uli == null) return null;
        if (!clients.containsKey(lii)) return null;
        if (rooms.maxRoomsReached()) return null;
        unsubscribeFromRoomUpdatesPrivate(lii);
        Room newRoom = new Room();
        newRoom.addPlayer(lii.getUsername());
        rooms.addRoom(newRoom);
        LobbyObject lobbyObj = new LobbyObject(this, newRoom);
        lobbyObj.addUser(lii, uli);
        synchronized (roomIdLobbyMap) {
            roomIdLobbyMap.put(newRoom.getId(), lobbyObj);
            roomIdRoomMap.put(newRoom.getId(), newRoom);
        }
        return lobbyObj;
    }

    @Override
    public synchronized LobbyInterface enterRoom(LoggedInInterface lii, long roomId, UpdateLobbyInterface uli) throws RemoteException {
        if (lii == null || uli == null) return null;
        if (!clients.containsKey(lii)) return null;
        Room room = roomIdRoomMap.get(roomId);
        if (room == null) return null;
        if (room.getCount() == 4) return null;
        unsubscribeFromRoomUpdatesPrivate(lii);
        room.addPlayer(lii.getUsername());
        LobbyObject lobbyObj = roomIdLobbyMap.get(roomId);
        lobbyObj.addUser(lii, uli);
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

    protected synchronized void removeRoom(Room room) {
        roomIdLobbyMap.remove(room.getId());
        roomIdRoomMap.remove(room.getId());
        rooms.removeRoom(room);
    }

    private synchronized void unsubscribeFromRoomUpdatesPrivate(LoggedInInterface lii) {
        clients.remove(lii);
    }

    protected synchronized void updateAllRooms() {
        clients.keySet().forEach(client -> new Thread(() -> updateClientRoom(client)).start());
    }

    private synchronized void updateClientRoom(LoggedInInterface client) {
        new Thread(() -> {
            try {
                clients.get(client).updateRooms(Rooms.copyOf(rooms));
            } catch (RemoteException e) {
                unsubscribeFromRoomUpdatesPrivate(client);
            }
        }).start();
    }
}
