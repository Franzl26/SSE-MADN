package dataAndMethods;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class Rooms implements Serializable {
    public static final int MAX_ROOMS = 5; // todo max rooms

    private final HashMap<Long, Room> rooms;
    private int count = 0;

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
        if (count == MAX_ROOMS) return;
        rooms.put(room.getId(),room);
        count++;
    }

    public synchronized void removeRoom(Room room) {
        if (!rooms.containsKey(room.getId())) return;
        rooms.remove(room.getId());
        count--;
    }

    public boolean maxRoomsReached() {
        return count == MAX_ROOMS;
    }

    public static Rooms copyOf(Rooms rooms) {
        return new Rooms(rooms.rooms);
    }

    public int getCount() {
        return count;
    }
}
