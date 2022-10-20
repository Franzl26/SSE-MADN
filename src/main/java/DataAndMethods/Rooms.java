package DataAndMethods;

import java.io.Serializable;
import java.util.Vector;

public class Rooms implements Serializable {
    private final Vector<Room> rooms;
    public static final int MAX_ROOMS = 5; // todo max rooms

    public Rooms() {
        rooms = new Vector<>(MAX_ROOMS);
    }

    public Vector<Room> getRooms() {
        return rooms;
    }

    public synchronized void addRoom(Room room) {
        if (rooms.size() == MAX_ROOMS) return;
        rooms.add(room);
    }

    public synchronized void removeRoom(Room room) {
        rooms.remove(room);
    }

    public boolean maxRoomsReached() {
        return rooms.size() == MAX_ROOMS;
    }


}
