package DataAndMethods;

import java.io.Serializable;
import java.util.ArrayList;

public class Rooms implements Serializable {
    private final ArrayList<Room> rooms;

    public Rooms() {
        rooms = new ArrayList<>();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }
}
