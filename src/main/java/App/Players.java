package App;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Players {
    private ArrayList<Player> players;

    public Players() {
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        if (players.size() == 4) throw new IllegalArgumentException("array already full");
        for (Player p : players) {
            if (p.getColor().equals(player.getColor()))
                throw new IllegalArgumentException("Color is already in use");
            if (p.getName().equals(player.getName()))
                throw new IllegalArgumentException("name already in array");
        }
        players.add(player);
    }

    public void addPlayer(String name, Color color) {
        if (players.size() == 4) throw new IllegalArgumentException("array already full");
        for (Player p : players) {
            if (p.getColor().equals(color))
                throw new IllegalArgumentException("Color is already in use");
            if (p.getName().equals(name))
                throw new IllegalArgumentException("name already in array");
        }
        players.add(new Player(name, color));
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int i) {
        return players.get(i);
    }

    public int getCount() {
        return players.size();
    }
}
