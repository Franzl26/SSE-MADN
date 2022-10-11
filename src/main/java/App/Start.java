package App;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;

public class Start extends Application {
    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);
        //RootPane root = new RootPane();
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 1300, 400);

        stage.setTitle("Questionnaire");
        stage.setScene(scene);
        stage.show();
        stage.hide();

        GamePane.GamePaneStart();
    }

    public static void main(String[] args) {
        //launch();
        hash();
    }

    public static void hash() {
        String password = "ILoveJava";

        MessageDigest md = null;
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
