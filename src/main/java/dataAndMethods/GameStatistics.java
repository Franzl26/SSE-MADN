package dataAndMethods;

import java.io.Serializable;
import java.util.Arrays;

public class GameStatistics implements Serializable {
    private final String[] names;
    private final int[][] zahlenGewuerfelt;
    private final int[] andereGeschlagen;
    private final int[] geschlagenWorden;
    private final int[] prioZugFalsch;
    private final String[] finishPlaces;
    private final long startTime;

    public GameStatistics(String[] names) {
        this.names = names;
        zahlenGewuerfelt = new int[4][6];
        andereGeschlagen = new int[4];
        geschlagenWorden = new int[4];
        finishPlaces = new String[4];
        prioZugFalsch = new int[4];
        startTime = System.currentTimeMillis();
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

    public void incPrioZugIgnoriert(int spieler) {
        prioZugFalsch[spieler]++;
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

    public int[] getPrioZugFalsch() {
        return prioZugFalsch;
    }

    public String[] getFinishPlaces() {
        return finishPlaces;
    }

    public long getStartTime() {
        return startTime;
    }

    public String[] getNames() {
        return names;
    }

    @Override
    public String toString() {
        return Arrays.toString(names) + "\nwuerfel\n" + Arrays.toString(zahlenGewuerfelt[0]) + "\n" + Arrays.toString(zahlenGewuerfelt[1]) + "\n" + Arrays.toString(zahlenGewuerfelt[2]) + "\n" + Arrays.toString(zahlenGewuerfelt[3]) + "\nandere geschlagen: " + Arrays.toString(andereGeschlagen) + "\ngeschlagenWorden: " + Arrays.toString(geschlagenWorden) + "\nprio ignoriert: " + Arrays.toString(prioZugFalsch) + "\nstartTime: " + startTime;
    }

    public GameStatistics() { // todo entfernen
        names = new String[]{"Domenik", "Tom", "Markus", "Nico"};
        geschlagenWorden = new int[]{12, 23, 2, 6};
        andereGeschlagen = new int[]{4, 23, 9, 10};
        zahlenGewuerfelt = new int[][]{{100, 110, 90, 97, 103, 120}, {100, 110, 90, 97, 103, 120}, {100, 110, 90, 97, 103, 120}, {100, 110, 90, 97, 103, 120}};
        prioZugFalsch = new int[]{0, 5, 0, 12};
        startTime = System.currentTimeMillis() - 14141;
        finishPlaces = new String[]{"Tom", "Markus", "Nico", "Domenik"};

    }
}
