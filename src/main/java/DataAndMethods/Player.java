package DataAndMethods;

import RMIInterfaces.LoggedInInterface;

public class Player {
    public LoggedInInterface lii;
    public String name;
    public final FieldState field;

    public Player(LoggedInInterface lii, String name, FieldState field) {
        this.lii = lii;
        this.name = name;
        this.field = field;
    }
}
