package Dialogs;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardState;
import RMIInterfaces.LoggedInInterface;
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

import static DataAndMethods.BoardDrawer.drawBoardAll;

public class DesignPane extends AnchorPane {
    private boolean last = false;

    public DesignPane(String[] designs) {

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
        cancelButton.addEventHandler(ActionEvent.ACTION, e -> ((Stage) getScene().getWindow()).close());
        ChoiceBox<String> boardChoice = new ChoiceBox<>();
        boardChoice.setOnHiding(e -> {
            if (last) {
                last = false;
                return;
            }
            last = true;
            BoardConfiguration config = CommunicationWithServer.getBoardConfig(boardChoice.getValue());
            BoardState state = new BoardState(4);
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
            CommunicationWithServer.designBestaetigen(boardChoice.getValue());
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
        getScene().getWindow().setOnCloseRequest(e -> ((Stage) getScene().getWindow()).close());
    }

    public static void DesignPaneStart(String[] designs) {
        DesignPane root = new DesignPane(designs);
        Scene scene = new Scene(root, 700, 520);
        Stage stage = new Stage();

        stage.setTitle("Design Auswählen");
        stage.setScene(scene);
        stage.setResizable(false);
        root.setOnClose();
        stage.show();
    }
}
