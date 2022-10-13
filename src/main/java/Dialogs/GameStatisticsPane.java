package Dialogs;

import App.GameStatistics;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GameStatisticsPane extends AnchorPane {
    private final GraphicsContext gc;

    public GameStatisticsPane() {
        Canvas canvas = new Canvas(400, 300);
        gc = canvas.getGraphicsContext2D();

        Button okayButton = new Button("Okay");
        okayButton.addEventHandler(ActionEvent.ACTION, e -> {

        });

        AnchorPane.setLeftAnchor(canvas, 10.0);
        AnchorPane.setTopAnchor(canvas, 10.0);
        AnchorPane.setRightAnchor(okayButton, 10.0);
        AnchorPane.setBottomAnchor(okayButton, 10.0);

        getChildren().addAll(canvas, okayButton);

        drawStatisticsTest();
    }

    public void drawStatistics(GameStatistics stats) {
        gc.setLineWidth(1.0);
        gc.setFont(Font.font(40));
        gc.fillText("Platz: " + stats.getFinishPlace(), 5, 30);
        gc.setFont(Font.font(30));
        gc.fillText("gewürfelte Zahlen", 5, 70);
        gc.setFont(Font.font(20));
        int totalThrows = stats.getTotalThrows();
        for (int i = 1; i < 7; i++) {
            int throwsSingle = stats.getThrows(i);
            double percent = ((throwsSingle * 10000 / totalThrows) / 100.0);
            gc.fillText(i + ": " + (throwsSingle < 100 ? "  " : "") + throwsSingle + " = "
                    + percent, 5, i * 20 + 90);
            gc.fillRect(140, i * 20 + 73, percent * 10, 18);
        }
        gc.setFont(Font.font(25));
        gc.fillText("andere Spieler geschlagen: " + stats.getPeopleKicked(), 5, 240);
        long time = stats.getGameDurationSeconds();
        gc.fillText("Spielzeit: " + (time / 3600) + ":" + ((time % 3600) / 60) + ":" + (time % 60) + " h", 5, 270);
    }

    public static void GameStatisticsPaneStart() {
        GameStatisticsPane root = new GameStatisticsPane();
        Scene scene = new Scene(root, 400, 330);
        Stage stage = new Stage();

        stage.setTitle("Spielstatistik");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void drawStatisticsTest() {
        GameStatistics stats = new GameStatistics();
        for (int i = 0; i < 100; i++) stats.incrementThrows(1);
        for (int i = 0; i < 110; i++) stats.incrementThrows(2);
        for (int i = 0; i < 90; i++) stats.incrementThrows(3);
        for (int i = 0; i < 97; i++) stats.incrementThrows(4);
        for (int i = 0; i < 103; i++) stats.incrementThrows(5);
        for (int i = 0; i < 120; i++) stats.incrementThrows(6);
        for (int i = 0; i < 45; i++) stats.incrementPeopleKicked();
        stats.setFinishPlace(2);
        stats.setStartTime(0);
        stats.setEndTime(754000);
        drawStatistics(stats);
    }
}
