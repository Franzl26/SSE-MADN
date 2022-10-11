package App;

import javafx.scene.paint.Color;

public class Players {
    private Player[] players;
    private int count = 0;

    public Players() {
        players = new Player[4];
    }

    public void addPlayer(Player player) {
        if (count == 4) throw new IllegalArgumentException("array already full");
        for (int i = 0; i < count; i++) {
            if (players[i].getColor().equals(player.getColor()))
                throw new IllegalArgumentException("Color is already in use");
            if (players[i].getName().equals(player.getName()))
                throw new IllegalArgumentException("name already in array");
        }
        players[count++] = player;
    }

    public void addPlayer(String name, Color color) {
        if (count == 4) throw new IllegalArgumentException("array already full");
        for (int i = 0; i < count; i++) {
            if (players[i].getColor().equals(color)) throw new IllegalArgumentException("Color is already in use");
            if (players[i].getName().equals(name)) throw new IllegalArgumentException("name already in array");
        }
        players[count++] = new Player(name, color);
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayer(int i) {
        return players[i];
    }

    public int getCount() {
        return count;
    }
}
