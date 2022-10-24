package dataAndMethods;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    private static long ID_COUNTER = 0;

    private final ArrayList<String> players;
    private final long id = ID_COUNTER++;
    public Room() {
        players = new ArrayList<>();
    }

    public void addPlayer(String name) {
        if (players.size() == 4) throw new IllegalArgumentException("array already full");
        for (String p : players) {
            if (p.equals(name)) throw new IllegalArgumentException("name already in array");
        }
        players.add(name);
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public int getCount() {
        return players.size();
    }

    public long getId() {
        return id;
    }
}
