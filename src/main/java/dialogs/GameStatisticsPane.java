package dialogs;

import dataAndMethods.GameStatistics;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class GameStatisticsPane extends AnchorPane {
    private final GraphicsContext gc;

    private GameStatisticsPane() {
        Canvas canvas = new Canvas(780, 710);
        gc = canvas.getGraphicsContext2D();

        Button okayButton = new Button("Okay");
        okayButton.addEventHandler(ActionEvent.ACTION, e -> verlassen());
        okayButton.setPrefWidth(200);
        okayButton.setPrefHeight(50);

        AnchorPane.setLeftAnchor(canvas, 10.0);
        AnchorPane.setTopAnchor(canvas, 10.0);
        AnchorPane.setRightAnchor(okayButton, 10.0);
        AnchorPane.setBottomAnchor(okayButton, 10.0);

        getChildren().addAll(canvas, okayButton);

    }

    public void drawStatistics(GameStatistics stats) {
        String[] names = stats.getNames();
        int anzahl = names.length;

        File f = new File("./resources/gradient.png");
        Image image = new Image(Paths.get(f.getAbsolutePath()).toUri().toString());

        gc.setFill(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.setFont(Font.font(40));
        String[] s = stats.getFinishPlaces();
        for (int i = 0; i < anzahl; i++) {
            gc.fillText("Platz " + (i + 1) + ": " + (s[i] != null ? s[i] : ""), 5, i * 40 + 30);
        }

        int[][] wuerfe = stats.getZahlenGewuerfelt();
        for (int i = 0; i < anzahl; i++) {
            int baseX = 5 + (i % 2) * 400;
            int baseY = 190 + (i > 1 ? 180 : 0);
            gc.setFont(Font.font(30));
            gc.fillText(names[i], baseX, baseY);
            gc.setFont(Font.font(20));
            int maxWurf = 0;
            int gesamt = 0;
            for (int j = 0; j < 6; j++) {
                gesamt += wuerfe[i][j];
                if (wuerfe[i][j] > maxWurf) maxWurf = wuerfe[i][j];
            }
            if (gesamt == 0) continue;
            for (int j = 1; j < 7; j++) {
                //noinspection IntegerDivisionInFloatingPointContext
                double percent = ((wuerfe[i][j - 1] * 10000 / gesamt) / 100.0);
                gc.fillText(j + ": " + (wuerfe[i][j - 1] < 100 ? "  " : "") + wuerfe[i][j - 1] + " = " + percent, baseX, baseY + j * 20 + 10);
                gc.drawImage(image, baseX + 135, baseY + j * 20 - 7, 200.0 * wuerfe[i][j-1] / maxWurf, 18);
            }
        }

        int[] andereGeschlagen = stats.getAndereGeschlagen();
        int[] geschlagenWorden = stats.getGeschlagenWorden();
        int[] prioZug = stats.getPrioZugFalsch();
        gc.setFont(Font.font(20));
        gc.fillText("andere geschlagen", 5, 570);
        gc.fillText("geschlagen worden", 5, 600);
        gc.fillText("Prio Zug falsch", 5, 630);
        for (int i = 0; i < anzahl; i++) {
            gc.setFont(Font.font(30));
            gc.fillText(names[i], 210 + i * 150, 540, 140);
            gc.setFont(Font.font(25));
            gc.fillText((andereGeschlagen[i] < 10 ? "  " : "") + andereGeschlagen[i], 230 + i * 150, 570);
            gc.fillText((geschlagenWorden[i] < 10 ? "  " : "") + geschlagenWorden[i], 230 + i * 150, 600);
            gc.fillText((prioZug[i] < 10 ? "  " : "") + prioZug[i], 230 + i * 150, 630);
        }
        gc.setFont(Font.font(45));
        long time = (System.currentTimeMillis() - stats.getStartTime()) / 1000;
        long h = time / 3600;
        long min = (time % 3600) / 60;
        long sek = (time % 60);
        gc.fillText("Spielzeit:    " + (h < 10 ? "0" + h : h) + ":" + (min < 10 ? "0" + min : min) + ":" + (sek < 10 ? "0" + sek : sek) + " h", 5, 690);
    }

    private void setOnClose() {
        getScene().getWindow().setOnCloseRequest(e -> {
            verlassen();
            e.consume();
        });
    }

    private void verlassen() {
        CommunicationWithServer.startNewRoomSelectPane();
        ((Stage) getScene().getWindow()).close();
    }

    public static GameStatisticsPane GameStatisticsPaneStart() {
        GameStatisticsPane root = new GameStatisticsPane();
        Scene scene = new Scene(root, 800, 730);
        Stage stage = new Stage();

        stage.setTitle("Spielstatistik");
        stage.setScene(scene);
        stage.setResizable(false);
        root.setOnClose();
        //stage.show();
        return root;
    }
}
