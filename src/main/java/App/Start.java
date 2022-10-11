package App;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Start extends Application {
    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);

        //GamePane.GamePaneStart();
        //LoginPane.LoginPaneStart();
        //RegisterPane.RegisterPaneStart();
        //LobbyPane.LobbyPaneStart();
        //RoomSelectPane.RoomSelectPaneStart();

    }

    public static void main(String[] args) {
        hash();
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
}
