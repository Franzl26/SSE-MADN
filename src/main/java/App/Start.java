package App;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Start extends Application {
    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);


        //GameStatisticsPane.GameStatisticsPaneStart();
        //DesignPane.DesignPaneStart();
        VideoTestPane.VideoTestPaneStart();
        //GameLogic.testBoardLogic();
        //LoginPane.LoginPaneStart();
        //RegisterPane.RegisterPaneStart();
        //LobbyPane.LobbyPaneStart();
        //RoomSelectPane.RoomSelectPaneStart("Username");

        //new Alert(Alert.AlertType.INFORMATION, "<Text>").show();
        //new Alert(Alert.AlertType.CONFIRMATION, "<Text>").show();

    }

    public static void main(String[] args) {
        //hash();
        //pathTest();
        //boardKonfigTest();
        launch();
    }

    public static void hash() {
        String password = "ILoveJava";

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(password.getBytes());
        byte[] digest = md.digest();
        System.out.println(bytesToHex(digest));
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static void pathTest() {
        File f = new File("./resources/waiting/");
        System.out.println(f.getAbsoluteFile());
        File[] arr = f.listFiles();
        f = arr[(int) (Math.random() * arr.length)];
        System.out.println(f.getAbsolutePath());
    }

    public static void boardKonfigTest() {
        BoardConfiguration konfig = BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard");
        System.out.println(konfig);
    }
}
