package DataAndMethods;

import java.io.Serializable;

public class GameStatistics implements Serializable {
    private final int[][] zahlenGewuerfelt;
    private final int[] andereGeschlagen;
    private final int[] geschlagenWorden;
    private final String[] finishPlaces;
    private long startTime;

    public GameStatistics(int spieler) {
        zahlenGewuerfelt = new int[spieler][6];
        andereGeschlagen = new int[spieler];
        geschlagenWorden = new int[spieler];
        finishPlaces = new String[spieler];
    }

    public void incZahlGewuerfelt(int spieler, int zahl) {
        zahlenGewuerfelt[spieler][zahl]++;
    }

    public void incAndereGeschlagen(int spieler) {
        andereGeschlagen[spieler]++;
    }

    public void incGeschlagenWorden(int spieler) {
        geschlagenWorden[spieler]++;
    }

    public void setFinish(int platz, String name) {
        finishPlaces[platz] = name;
    }

    public int[][] getZahlenGewuerfelt() {
        return zahlenGewuerfelt;
    }

    public int[] getAndereGeschlagen() {
        return andereGeschlagen;
    }

    public int[] getGeschlagenWorden() {
        return geschlagenWorden;
    }

    public String[] getFinishPlaces() {
        return finishPlaces;
    }

    public void setStartTime() {
        startTime = System.currentTimeMillis();
    }
}
