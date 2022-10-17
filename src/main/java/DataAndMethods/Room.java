package DataAndMethods;

public class Room {
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
}
