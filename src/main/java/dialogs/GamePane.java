package dialogs;

import dataAndMethods.BoardConfiguration;
import dataAndMethods.BoardState;
import dataAndMethods.FieldState;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

import static dataAndMethods.BoardDrawer.drawBoardAll;
import static dataAndMethods.BoardDrawer.drawBoardSingleFieldAll;

public class GamePane extends AnchorPane {
    private final GameLogic logic;
    private final BoardConfiguration config;
    private final GraphicsContext gcBoard;
    private final GraphicsContext gcDice;
    private final GraphicsContext gcName;
    private final GraphicsContext gcGif;
    private final Canvas gifCanvas;
    private final MediaView gifView;
    private File[] picturesArray = null;
    private File[] gifsArray = null;

    public GamePane(GameLogic logic) {
        this.logic = logic;
        config = logic.boardConfig;

        setBackground(Background.fill(Color.LIGHTSLATEGRAY));
        Canvas nameCanvas = new Canvas(980, 50);
        gcName = nameCanvas.getGraphicsContext2D();

        Canvas diceCanvas = new Canvas(100, 100);
        gcDice = diceCanvas.getGraphicsContext2D();
        diceCanvas.setOnMouseClicked(e -> logic.onMouseClickedDice());

        Canvas boardCanvas = new Canvas(500, 500);
        gcBoard = boardCanvas.getGraphicsContext2D();
        boardCanvas.setOnMouseClicked(e -> logic.onMouseClickedField(e.getX(), e.getY()));

        gifCanvas = new Canvas(360, 360);
        gcGif = gifCanvas.getGraphicsContext2D();
        gcGif.setFill(Color.LIGHTSLATEGRAY);
        gcGif.fillRect(0, 0, 360, 360);
        //gifCanvas.setOnMouseClicked(e -> showGif());

        gifView = new MediaView();
        gifView.setFitWidth(360);
        gifView.setFitHeight(360);
        //gifView.setOnMouseClicked(e -> showGif());

        Button spielVerlassenButton = new Button("Spiel verlassen");
        spielVerlassenButton.addEventHandler(ActionEvent.ACTION, e -> spielVerlassen());

        AnchorPane.setLeftAnchor(nameCanvas, 10.0);
        AnchorPane.setTopAnchor(nameCanvas, 10.0);
        AnchorPane.setLeftAnchor(diceCanvas, 10.0);
        AnchorPane.setBottomAnchor(diceCanvas, 210.0);
        AnchorPane.setLeftAnchor(boardCanvas, 120.0);
        AnchorPane.setBottomAnchor(boardCanvas, 10.0);
        AnchorPane.setRightAnchor(gifCanvas, 10.0);
        AnchorPane.setTopAnchor(gifCanvas, 90.0);
        AnchorPane.setRightAnchor(gifView, 10.0);
        AnchorPane.setTopAnchor(gifView, 90.0);
        AnchorPane.setRightAnchor(spielVerlassenButton, 10.0);
        AnchorPane.setBottomAnchor(spielVerlassenButton, 10.0);

        getChildren().addAll(nameCanvas, boardCanvas, diceCanvas, gifCanvas, gifView, spielVerlassenButton);
        // init Gifs
        try {
            File f = new File("./resources/waiting/pictures/");
            picturesArray = f.listFiles();
            f = new File("./resources/waiting/gifs/");
            gifsArray = f.listFiles();
        } catch (NullPointerException e) {
            System.out.println("no gifs found");
        }
    }

    public void drawDice(int number) {
        gcDice.drawImage(config.dice[number], 0, 0, 100, 100);
    }

    public void drawBoard(BoardState board) {
        drawBoardAll(gcBoard, config, board);
    }

    public void drawBoardSingleField(FieldState state, int i, boolean highlight) {
        drawBoardSingleFieldAll(gcBoard, config, state, i, highlight);
    }

    public void drawNames(String[] players, int turn) {
        gcName.setFill(Color.LIGHTSLATEGRAY);
        gcName.fillRect(0, 0, 980, 50);
        gcName.setFont(Font.font(40));
        gcName.setFill(Color.BLACK);
        for (int i = 0; i < players.length; i++) {
            gcName.drawImage(config.figure[(players.length == 2 ? (i == 0 ? 0 : 2) : i)], 5 + 245 * i, 5, 40, 40);
            String p = players[i];
            gcName.fillText(p, i * 245 + 50, 40, 190);
            if (i == turn) gcName.fillRect(i * 245 + 5, 46, 235, 47);
        }
    }

    public void showGif() {
        gcGif.setFill(Color.LIGHTSLATEGRAY);
        gcGif.fillRect(0, 0, 360, 360);

        double rand = Math.random();
        if (picturesArray == null) {
            System.err.println("couldn't print gif/picture");
            rand = Math.random();
        }
        if (rand > (1.0 * gifsArray.length / (gifsArray.length + picturesArray.length))) {
            gifCanvas.toFront();
            File f = picturesArray[(int) (Math.random() * picturesArray.length)];
            Image image = new Image(Paths.get(f.getAbsolutePath()).toUri().toString(), 360, 360, true, false);
            gcGif.drawImage(image, 0, 0);
        } else {
            gifView.toFront();
            File f = gifsArray[(int) (Math.random() * gifsArray.length)];
            Media media = new Media(f.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            gifView.setMediaPlayer(player);
            player.setAutoPlay(true);
            player.setCycleCount(50);
        }
    }

    public void hideGif() {
        gcGif.setFill(Color.LIGHTSLATEGRAY);
        gcGif.fillRect(0, 0, 360, 360);
        gifCanvas.toFront();
    }

    private void spielVerlassen() {
        if (Meldungen.frageBestaetigung("Spiel verlassen", "Willst du das Spiel wirklich verlassen?")) {
            logic.spielVerlassen();
            ((Stage) getScene().getWindow()).close();
        }
    }

    private void setOnClose() {
        getScene().getWindow().setOnCloseRequest(e -> {
            spielVerlassen();
            e.consume();
        });
    }

    public static GamePane GamePaneStart(GameLogic logic) {
        GamePane root = new GamePane(logic);
        Scene scene = new Scene(root, 1000, 600);
        Stage stage = new Stage();

        stage.setTitle("Mensch Ärgere dich nicht");
        stage.setScene(scene);
        stage.setResizable(false);
        //stage.show();
        root.setOnClose();
        return root;
    }
}
