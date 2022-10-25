package dataAndMethods;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class Rooms implements Serializable {
    public static final int MAX_ROOMS = 5; // todo max rooms

    private final HashMap<Long, Room> rooms;

    private Rooms(HashMap<Long, Room> rooms) {
        this.rooms = rooms;
    }

    public Rooms() {
        rooms = new HashMap<>(MAX_ROOMS,1);
    }

    public Collection<Room> getRooms() {
        return rooms.values();
    }

    public synchronized void addRoom(Room room) {
        if (rooms.size() == MAX_ROOMS) return;
        rooms.put(room.getId(),room);
    }

    public synchronized void removeRoom(Room room) {
        rooms.remove(room.getId());
    }

    public boolean maxRoomsReached() {
        return rooms.size() == MAX_ROOMS;
    }

    public static Rooms copyOf(Rooms rooms) {
        return new Rooms(rooms.rooms);
    }
}
