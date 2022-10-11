package App;

import java.util.ArrayList;

public class Rooms {
    private ArrayList<Room> rooms;

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
