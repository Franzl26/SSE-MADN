package NoLongerInUse;

import javafx.scene.paint.Color;

public class Player {
    private final String name;
    private final Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        throw new UnsupportedOperationException("No longer in use");
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}