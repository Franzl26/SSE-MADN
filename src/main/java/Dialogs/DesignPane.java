package Dialogs;

import ClientLogic.CommunicationWithServer;
import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardConfigurationBytes;
import DataAndMethods.BoardState;
import RMIInterfaces.UpdateLobbyInterface;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

import static DataAndMethods.BoardDrawer.drawBoardAll;

public class DesignPane extends AnchorPane {
    private boolean last = false;
    private final UpdateLobbyInterface uli;

    public DesignPane(String[] designs, UpdateLobbyInterface uli) {
        this.uli = uli;

        setBackground(Background.fill(Color.LIGHTSLATEGRAY));
        Canvas boardCanvas = new Canvas(500, 500);
        GraphicsContext gcBoard = boardCanvas.getGraphicsContext2D();
        Canvas diceCanvas = new Canvas(100, 100);
        GraphicsContext gcDice = diceCanvas.getGraphicsContext2D();

        gcBoard.setFill(Color.LIGHTSLATEGRAY);
        gcBoard.fillRect(0, 0, 500, 500);
        gcDice.setFill(Color.LIGHTSLATEGRAY);
        gcDice.fillRect(0, 0, 100, 100);

        Button cancelButton = new Button("Abbrechen");
        cancelButton.setPrefWidth(80);
        cancelButton.addEventHandler(ActionEvent.ACTION, e -> {
            CommunicationWithServer.designAendernAbbrechen();
            ((Stage) getScene().getWindow()).close();
        });
        ChoiceBox<String> boardChoice = new ChoiceBox<>();
        boardChoice.setOnHiding(e -> {
            if (last) {
                last = false;
                return;
            }
            last = true;
            BoardConfiguration config = getBoardConfig(boardChoice.getValue());
            BoardState state = new BoardState();
            state.reset();
            drawBoardAll(gcBoard, config, state);
            gcDice.setFill(Color.LIGHTSLATEGRAY);
            gcDice.fillRect(0, 0, 100, 100);
            gcDice.drawImage(config.dice[5], 0, 0, 100, 100);
        });
        boardChoice.setPrefWidth(170);
        boardChoice.getItems().addAll(designs);
        boardChoice.setValue("Standard");

        Button selectButton = new Button("Auswählen");
        selectButton.setPrefWidth(80);
        selectButton.addEventHandler(ActionEvent.ACTION, e -> {
            CommunicationWithServer.designBestaetigen(uli, boardChoice.getValue());
            ((Stage)getScene().getWindow()).close();
        });

        AnchorPane.setLeftAnchor(boardCanvas, 10.0);
        AnchorPane.setTopAnchor(boardCanvas, 10.0);
        AnchorPane.setLeftAnchor(diceCanvas, 540.0);
        AnchorPane.setTopAnchor(diceCanvas, 50.0);
        AnchorPane.setRightAnchor(selectButton, 10.0);
        AnchorPane.setBottomAnchor(selectButton, 10.0);
        AnchorPane.setRightAnchor(cancelButton, 100.0);
        AnchorPane.setBottomAnchor(cancelButton, 10.0);
        AnchorPane.setRightAnchor(boardChoice, 10.0);
        AnchorPane.setBottomAnchor(boardChoice, 150.0);

        getChildren().addAll(boardCanvas, diceCanvas, cancelButton, selectButton, boardChoice);
        boardChoice.hide();
    }

    private void setOnClose() {
        getScene().getWindow().setOnCloseRequest(e -> {
            CommunicationWithServer.designAendernAbbrechen();
            ((Stage) getScene().getWindow()).close();
        });
    }

    public static DesignPane DesignPaneStart(String[] designs, UpdateLobbyInterface uli) {
        DesignPane root = new DesignPane(designs, uli);
        Scene scene = new Scene(root, 700, 520);
        Stage stage = new Stage();

        stage.setTitle("Design Auswählen");
        stage.setScene(scene);
        stage.setResizable(false);
        root.setOnClose();
        stage.show();
        return root;
    }

    private static final String[] compareList = new String[]{"board.png", "dice0.png", "dice1.png", "dice2.png",
            "dice3.png", "dice4.png", "dice5.png", "dice6.png", "dice7.png", "figure0.png", "figure1.png", "figure2.png",
            "figure3.png", "figureHigh0.png", "figureHigh1.png", "figureHigh2.png", "figureHigh3.png", "path0.png",
            "path1.png", "path2.png", "path3.png", "pathNormal.png", "personal0.png", "personal1.png", "personal2.png",
            "personal3.png", "positions.txt"};


    public BoardConfiguration getBoardConfig(String name) {
        File file = new File("./resources/designs/" + name + "/");
        if (!(file.isDirectory() && Arrays.equals(file.list(), compareList))) {
            file.mkdir();
            BoardConfigurationBytes config = CommunicationWithServer.getBoardConfig(uli, name);
            config.saveConfiguration(file.getAbsolutePath());
        }
        return BoardConfiguration.loadBoardKonfiguration(file.getAbsolutePath());
    }
}
