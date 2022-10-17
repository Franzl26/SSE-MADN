package DataAndMethods;

public class GameStatistics {
    private final int[] numbersThrown = new int[6];
    private int peopleKicked = 0;
    private long startTime;
    private long endTime;
    private int finishPlace;

    public int getFinishPlace() {
        return finishPlace;
    }

    public void setFinishPlace(int finishPlace) {
        this.finishPlace = finishPlace;
    }

    public void incrementThrows(int zahl) {
        numbersThrown[zahl - 1]++;
    }

    public int getTotalThrows() {
        int temp = 0;
        for (int i = 0; i < 6; i++) {
            temp += numbersThrown[i];
        }
        return temp;
    }

    public int getThrows(int zahl) {
        return numbersThrown[zahl - 1];
    }

    public void incrementPeopleKicked() {
        peopleKicked++;
    }

    public int getPeopleKicked() {
        return peopleKicked;
    }

    public void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    public void setEndTime() {
        endTime = System.currentTimeMillis();
    }

    public void setStartTime(long millis) {
        startTime = millis;
    }

    public void setEndTime(long millis) {
        endTime = millis;
    }

    public long getGameDurationSeconds() {
        return (endTime - startTime) / 1000;
    }
}
