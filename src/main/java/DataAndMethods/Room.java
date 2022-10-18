package DataAndMethods;

import java.io.Serializable;

public class Room implements Serializable {
    private final Players players;

    public Room() {
        players = new Players();
    }

    public void addPlayer(String player) {
        players.addPlayer(player);
    }

    public Players getPlayers() {
        return players;
    }

    public int getCount() {
        return players.getCount();
    }
}
