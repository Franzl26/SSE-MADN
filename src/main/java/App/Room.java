package App;

public class Room {
    private static int id_counter = 0;
    private int id;
    private Players players;

    public Room() {
        id = id_counter++;
    }

    public void addPlayer(Player player) {
        players.addPlayer(player);
    }

    public Players getPlayers() {
        return players;
    }

    public int getId() {
        return id;
    }
}
