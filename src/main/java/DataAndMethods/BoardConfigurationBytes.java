package DataAndMethods;

import java.io.*;

public class BoardConfigurationBytes implements Serializable {
    private final byte[] position;
    private final byte[] board;
    private final byte[] pathNormal;
    private final byte[][] dice;
    private final byte[][] path;
    private final byte[][] personal;
    private final byte[][] figure;
    private final byte[][] figureHigh;

    private BoardConfigurationBytes(Builder builder) {
        position = builder.position;
        board = builder.board;
        pathNormal = builder.pathNormal;
        dice = builder.dice;
        path = builder.path;
        personal = builder.personal;
        figure = builder.figure;
        figureHigh = builder.figureHigh;
    }

    public static BoardConfigurationBytes loadBoardKonfiguration(String dir) {
        if (!dir.endsWith("/")) dir = dir.concat("/");
        Builder builder = new Builder();
        if (!builder.read(dir)) throw new RuntimeException("Could not load board configuration");
        return builder.build();
    }

    private static class Builder {
        private byte[] position;
        private byte[] board;
        private byte[] pathNormal;
        private byte[][] dice;
        private byte[][] path;
        private byte[][] personal;
        private byte[][] figure;
        private byte[][] figureHigh;

        private Builder() {
            dice = new byte[8][];
            path = new byte[4][];
            personal = new byte[4][];
            figure = new byte[4][];
            figureHigh = new byte[4][];
        }

        private boolean read(String dir) {
            // Bilder einlesen
            File f = new File(dir);
            if (!f.isDirectory()) return false;
            board = readFile(f.getAbsolutePath() + "/board.png");
            pathNormal = readFile(f.getAbsolutePath() + "/pathNormal.png");
            dice = readFiles(f.getAbsolutePath(), "/dice", 8);
            path = readFiles(f.getAbsolutePath(), "/path", 4);
            personal = readFiles(f.getAbsolutePath(), "/personal", 4);
            figure = readFiles(f.getAbsolutePath(), "/figure", 4);
            figureHigh = readFiles(f.getAbsolutePath(), "/figureHigh", 4);
            // position.txt einlesen
            position = readFile(f.getAbsolutePath() + "/positions.txt");
            return true;
        }

        private BoardConfigurationBytes build() {
            return new BoardConfigurationBytes(this);
        }

        private static byte[][] readFiles(String dir, String name, int count) {
            byte[][] tmp = new byte[count][];
            for (int i = 0; i < count; i++) {
                tmp[i] = readFile(dir + name + i + ".png");
            }
            return tmp;
        }

        private static byte[] readFile(String path) {
            File f = new File(path);
            try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
                int length = (int) f.length();
                byte[] file = new byte[length];
                dis.readFully(file);
                return file;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveConfiguration(String dir) {
        if (!dir.endsWith("/")) dir = dir.concat("/");
        saveFile(dir+"board.png",board);
        saveFile(dir+"pathNormal.png",pathNormal);
        saveFiles(dir, "dice", dice);
        saveFiles(dir, "figure", figure);
        saveFiles(dir, "figureHigh", figureHigh);
        saveFiles(dir, "path", path);
        saveFiles(dir, "personal", personal);
        saveFile(dir+"positions.txt",position);
    }

    private void saveFiles(String dir, String name, byte[][] data) {
        for (int i = 0; i < data.length; i++) {
            saveFile(dir + name + i + ".png", data[i]);
        }
    }

    private void saveFile(String dir, byte[] data) {
        File f = new File(dir);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(f))) {
            dos.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
