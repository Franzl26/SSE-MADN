package App;

import javafx.scene.image.Image;

import java.io.File;

public class BoardKonfiguration {
    public Image board;
    public Image[] dice;
    public Image pathNormal;
    public Image[] path;
    public Image[] personal;
    public Image[] figure;
    public Image[] figureHigh;

    public BoardKonfiguration(String dir) {
        File f = new File(dir);
        System.out.println(f.isDirectory());
    }
}
