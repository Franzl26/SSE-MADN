package DataAndMethods;

import javafx.scene.image.Image;

import java.io.*;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardConfiguration {
    public final int[][] pointCoordinates;
    public final int[][] orientation;
    public final Image board;
    public final Image pathNormal;
    public final Image[] dice;
    public final Image[] path;
    public final Image[] personal;
    public final Image[] figure;
    public final Image[] figureHigh;
    public static final int clickRadius = 17;

    private BoardConfiguration(Builder builder) {
        pointCoordinates = new int[72][2];
        orientation = new int[72][2];
        dice = new Image[8];
        path = new Image[4];
        personal = new Image[4];
        figure = new Image[4];
        figureHigh = new Image[4];

        int len = builder.pointCoordinates.length;
        for (int i = 0; i < len; i++) {
            System.arraycopy(builder.pointCoordinates[i], 0, pointCoordinates[i], 0, 2);
        }
        for (int i = 0; i < len; i++) {
            System.arraycopy(builder.orientation[i], 0, orientation[i], 0, 2);
        }
        board = builder.board;
        pathNormal = builder.pathNormal;
        System.arraycopy(builder.dice, 0, dice, 0, 8);
        System.arraycopy(builder.path, 0, path, 0, 4);
        System.arraycopy(builder.personal, 0, personal, 0, 4);
        System.arraycopy(builder.figure, 0, figure, 0, 4);
        System.arraycopy(builder.figureHigh, 0, figureHigh, 0, 4);
    }

    public static BoardConfiguration loadBoardKonfiguration(String dir) {
        Builder builder = new Builder();
        if (!builder.read(dir)) throw new LoadBoardConfigurationException("Could not load board configuration");
        return builder.build();
    }

    private static class Builder {
        private final int[][] pointCoordinates;
        private final int[][] orientation;
        private Image board;
        private Image pathNormal;
        private Image[] dice;
        private Image[] path;
        private Image[] personal;
        private Image[] figure;
        private Image[] figureHigh;

        private Builder() {
            pointCoordinates = new int[72][2];
            orientation = new int[72][2];
            dice = new Image[8];
            path = new Image[4];
            personal = new Image[4];
            figure = new Image[4];
            figureHigh = new Image[4];
        }

        private boolean read(String dir) {
            // Bilder einlesen
            try {
                File f = new File(dir);
                if (!f.isDirectory()) return false;
                board = new Image(Paths.get(f.getAbsolutePath()+"/board.png").toUri().toString());
                pathNormal = new Image(Paths.get(f.getAbsolutePath() + "/pathNormal.png").toUri().toString());
                dice = readImages(f.getAbsolutePath(), "/dice", 8);
                path = readImages(f.getAbsolutePath(), "/path", 4);
                personal = readImages(f.getAbsolutePath(), "/personal", 4);
                figure = readImages(f.getAbsolutePath(), "/figure", 4);
                figureHigh = readImages(f.getAbsolutePath(), "/figureHigh", 4);
            } catch (NullPointerException|IllegalArgumentException e) {
                e.printStackTrace(System.out);
                return false;
            }

            // Koordinaten einlesen
            File positions = new File(dir + "/positions.txt");
            try (BufferedReader buf = new BufferedReader(new FileReader(positions))) {
                //Pattern pattern = Pattern.compile("[ \t]*(\\d+)[ \t]+(\\d+)[ \t]*");
                Pattern pattern = Pattern.compile("[ \t]*(\\d+)[ \t]+(\\d+)[ \t]*([ \t]+(-?\\d+)([ \t]+([01])[ \t]*)?)?");
                String in = buf.readLine();
                int line = 0;
                int count = 0;
                while (in != null) {
                    line++;
                    if (!in.contains("//")) {
                        Matcher matcher = pattern.matcher(in);
                        if (!matcher.matches()) throw new IOException("position file has error on line:" + line);
                        pointCoordinates[count][0] = Integer.parseInt(matcher.group(1));
                        pointCoordinates[count][1] = Integer.parseInt(matcher.group(2));
                        if (matcher.group(4) != null) {
                            orientation[count][0] = Integer.parseInt(matcher.group(4));
                        }
                        if (matcher.group(6) != null) {
                            orientation[count][1] = Integer.parseInt(matcher.group(6));
                        }
                        count++;
                    }
                    in = buf.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace(System.out);
                return false;
            }

            return true;
        }

        private BoardConfiguration build() {
            return new BoardConfiguration(this);
        }

        private static Image[] readImages(String dir, String name, int count) {
            Image[] temp = new Image[count];
            for (int i = 0; i < count; i++) {
                temp[i] = new Image(Paths.get(dir + name + i + ".png").toUri().toString());
            }
            return temp;
        }

    }
}
