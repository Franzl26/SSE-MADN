package dataAndMethods;

import java.io.Serializable;

public class Room implements Serializable {
    private static long ID_COUNTER = 0;

    private final long id = ID_COUNTER++;
    private final String[] players = new String[4];
    private int count = 0;

    public synchronized void addPlayer(String name) {
        players[count++] = name;
    }

    public synchronized String[] getPlayers() {
        return players;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized long getId() {
        return id;
    }

    public synchronized void removePlayer(int i) {
        if (i != 3) System.arraycopy(players, i + 1, players, i, 3 - i);
        count--;
        players[3] = null;
    }
}
