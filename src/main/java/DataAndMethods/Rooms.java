package DataAndMethods;

import java.io.Serializable;
import java.util.Vector;

public class Rooms implements Serializable {
    private final Vector<Room> rooms;
    public static final int MAX_ROOMS = 25;

    public Rooms() {
        rooms = new Vector<>(MAX_ROOMS); // todo max rooms
    }

    public Vector<Room> getRooms() {
        return rooms;
    }

    /**
     * @return -1 maximale Anzahl erreicht, 1 erfolgreich
     */
    public synchronized int addRoom(Room room) {
        if (rooms.size() == MAX_ROOMS) return -1;
        rooms.add(room);
        return 1;
    }

    public synchronized void removeRoom(Room room) {
        rooms.remove(room);
    }

    public boolean maxRoomsReached() {
        return rooms.size() == MAX_ROOMS;
    }


}
