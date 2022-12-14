package dialogs;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class LobbyPane extends AnchorPane {
    private final GraphicsContext gcName;
    private String design = "Standard";

    public LobbyPane() {
        Canvas nameCanvas = new Canvas(200, 150);
        gcName = nameCanvas.getGraphicsContext2D();

        Button botAddButton = new Button("Bot hinzufügen");
        botAddButton.setPrefWidth(100);
        botAddButton.addEventHandler(ActionEvent.ACTION, e -> {
            int ret = CommunicationWithServer.addBot();
            if (ret == -1) {
                Meldungen.zeigeInformation("Warteraum voll", "Der Warteraum ist bereits voll, du kannst keinen Bot hinzufügen.");
            }
        });
        Button botRemoveButton = new Button("Bot entfernen");
        botRemoveButton.setPrefWidth(100);
        botRemoveButton.addEventHandler(ActionEvent.ACTION, e -> {
            int ret = CommunicationWithServer.removeBot();
            if (ret == -1) {
                Meldungen.zeigeInformation("Kein Bot im Warteraum", "Es befindet sich kein Bot im Warteraum, der entfernt werden kann.");
            }
        });
        Button designButton = new Button("Spieldesign auswählen");
        designButton.setPrefWidth(140);
        designButton.addEventHandler(ActionEvent.ACTION, e -> CommunicationWithServer.designAnpassen());
        Button startButton = new Button("Spiel starten");
        startButton.setPrefWidth(140);
        startButton.addEventHandler(ActionEvent.ACTION, e -> {
            int ret = CommunicationWithServer.spielStartenAnfragen();
            if (ret == -1) {
                Meldungen.zeigeInformation("Nicht genug Spieler im Warteraum", "Es sind weniger als 2 Spieler im Warteraum, dass Spiel kann nicht gestartet werden.");
            } else {
                ((Stage) getScene().getWindow()).close();
            }
        });
        Button exitButton = new Button("Warteraum verlassen");
        exitButton.setPrefWidth(140);
        exitButton.addEventHandler(ActionEvent.ACTION, e -> verlassen());

        AnchorPane.setLeftAnchor(nameCanvas, 10.0);
        AnchorPane.setTopAnchor(nameCanvas, 10.0);
        AnchorPane.setLeftAnchor(botAddButton, 10.0);
        AnchorPane.setBottomAnchor(botAddButton, 10.0);
        AnchorPane.setLeftAnchor(botRemoveButton, 120.0);
        AnchorPane.setBottomAnchor(botRemoveButton, 10.0);
        AnchorPane.setRightAnchor(exitButton, 10.0);
        AnchorPane.setBottomAnchor(exitButton, 10.0);
        AnchorPane.setRightAnchor(startButton, 10.0);
        AnchorPane.setBottomAnchor(startButton, 40.0);
        AnchorPane.setRightAnchor(designButton, 10.0);
        AnchorPane.setBottomAnchor(designButton, 100.0);

        getChildren().addAll(nameCanvas, botAddButton, botRemoveButton, designButton, startButton, exitButton);
    }

    private void setOnClose() {
        getScene().getWindow().setOnCloseRequest(e -> {
            verlassen();
            e.consume();
        });
    }

    private void verlassen() {
        if (Meldungen.frageBestaetigung("Warteraum verlassen", "Willst du den Warteraum wirklich verlassen und zur Raumauswahl zurückkehren?")) {
            CommunicationWithServer.raumVerlassen();
            ((Stage) getScene().getWindow()).close();
        }
    }

    public void drawNames(String[] names) {
        gcName.clearRect(0, 0, 200, 150);
        gcName.setLineWidth(1.0);
        gcName.setFont(Font.font(20));
        gcName.setFill(Color.BLACK);
        for (int i = 0; i < names.length; i++) {
            gcName.fillText(names[i], 5, i * 30 + 20, 190);
        }
    }

    public void gameStarts() {
        int ret = CommunicationWithServer.spielStartet(design);
        if (ret == 1) {
            ((Stage) getScene().getWindow()).close();
        }
    }

    public static LobbyPane LobbyPaneStart() {
        LobbyPane root = new LobbyPane();
        Scene scene = new Scene(root, 400, 200);
        Stage stage = new Stage();

        stage.setTitle("Warteraum");
        stage.setScene(scene);
        stage.setResizable(false);
        root.setOnClose();
        //stage.show();
        return root;
    }

    public void setDesign(String design) {
        this.design = design;
    }
}
