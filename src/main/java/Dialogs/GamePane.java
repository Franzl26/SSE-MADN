package Dialogs;

import ClientLogic.GameLogic;
import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import DataAndMethods.FieldState;
import DataAndMethods.Players;
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

import java.io.*;
import java.nio.file.Paths;

import static DataAndMethods.BoardDrawer.drawBoardAll;
import static DataAndMethods.BoardDrawer.drawBoardSingleFieldAll;

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
        gifCanvas.setOnMouseClicked(e -> showGif());

        gifView = new MediaView();
        gifView.setFitWidth(360);
        gifView.setFitHeight(360);
        gifView.setOnMouseClicked(e -> showGif());

        Button spielVerlassenButton = new Button("Spiel verlassen");
        spielVerlassenButton.addEventHandler(ActionEvent.ACTION, e -> System.exit(0));

        // todo remove
        Button test = new Button("TestPrio");
        test.setPrefWidth(100);
        test.setPrefHeight(70);
        test.addEventHandler(ActionEvent.ACTION, e -> logic.testButton());
        AnchorPane.setRightAnchor(test, 30.0);
        AnchorPane.setBottomAnchor(test, 70.0);


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

        getChildren().addAll(nameCanvas, boardCanvas, diceCanvas, gifCanvas, gifView, spielVerlassenButton, test);

        // init Gifs
        try {
            File f = new File("./resources/waiting/pictures/");
            picturesArray = f.listFiles();
            f = new File("./resources/waiting/gifs/");
            gifsArray = f.listFiles();
        } catch (NullPointerException e) {
            System.out.println("no gifs found");
        }

        testGameInit(this);
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

    public void drawNames(Players players, int turn) {
        gcName.setLineWidth(1.0);
        gcName.setFont(Font.font(40));
        gcName.setFill(Color.BLACK);

        for (int i = 0; i < players.getCount(); i++) {
            gcName.drawImage(config.figure[i], 5 + 245 * i, 5, 40, 40);
            String p = players.getPlayer(i);
            gcName.fillText(p, i * 245 + 50, 40, 190);
            //gcName.drawImage(config.personal[i], 200 + 245 * i, 5, 40, 40);

            if (i == turn) gcName.fillRect(i * 245 + 5, 46, 235, 47);
        }
    }

    public void showGif() {
        gcGif.setFill(Color.LIGHTSLATEGRAY);
        gcGif.fillRect(0, 0, 360, 360);

        double rand = Math.random();
        if (picturesArray == null) {
            System.out.println("couldn't print pic");
            rand = 0.1;
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
            player.setCycleCount(10);
        }
    }

    public static GamePane GamePaneStart(GameLogic logic) {
        GamePane root = new GamePane(logic);
        Scene scene = new Scene(root, 1000, 600);
        Stage stage = new Stage();

        stage.setTitle("Mensch Ärgere dich nicht");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        return root;
    }


    private void testGameInit(GamePane pane) {
        pane.drawDice(7);

        Players players = new Players();
        players.addPlayer("Tom");
        players.addPlayer("Nico");
        players.addPlayer("Markus");
        players.addPlayer("Domenik");
        pane.drawNames(players, 2);
        showGif();
    }
}
