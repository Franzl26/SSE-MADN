package App;

public class Room {
    private static int id_counter = 0;
    private final int id;
    private final Players players;

    public Room() {
        players = new Players();
        id = id_counter++;
    }

    public void addPlayer(String player) {
        players.addPlayer(player);
    }

    public Players getPlayers() {
        return players;
    }

    public int getId() {
        return id;
    }
}
