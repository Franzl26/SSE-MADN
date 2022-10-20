package DataAndMethods;

public class GameStatistics {
    private final int[][] zahlenGewuerfelt = new int[4][6];
    private final int[] andereGeschlagen = new int[4];
    private final int[] geschlagenWorden = new int[4];
    private final String[] finishPlaces = new String[4];
    private long startTime;
    private long endTime;

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

    public void setEndTime() {
        endTime = System.currentTimeMillis();
    }

    public long getGameDurationSeconds() {
        return (endTime - startTime) / 1000;
    }
}
