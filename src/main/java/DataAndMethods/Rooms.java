package DataAndMethods;

import java.util.ArrayList;

public class Rooms {
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
