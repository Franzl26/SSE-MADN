package App;

import Dialogs.*;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.crypto.*;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Locale;

public class Start extends Application {
    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);


        //LoginPane.LoginPaneStart();
        //RegisterPane.RegisterPaneStart();
        //RoomSelectPane.RoomSelectPaneStart("Username");
        //LobbyPane.LobbyPaneStart();
        //DesignPane.designsTest();
        GameLogic.testBoardLogic();
        //GameStatisticsPane.GameStatisticsPaneStart();

        //new Alert(Alert.AlertType.INFORMATION, "<Text>").show();
        //new Alert(Alert.AlertType.CONFIRMATION, "<Text>").show();

    }

    public static void main(String[] args) {
        //hash();
        //pathTest();
        //boardConfigTest();
        kryptoTest();
        //launch();
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

    public static void boardConfigTest() {
        BoardConfiguration config = BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard");
        System.out.println(config);
    }

    public static void kryptoTest() {
        try {
            // Schlüssel erzeugen
            SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
            // Verfahren wählen
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // Umwandlung des Strings nach Bytes basierend auf UTF-8
            byte[] utf8Bytes = "Zu verschlüsselnder String".getBytes();
            // Verschlüsselung
            byte[] encryptedBytes = cipher.doFinal(utf8Bytes);
            // Base64 encoding um wieder einen String zu bekommen
            String encryptedString = java.util.Base64.getEncoder().encodeToString(encryptedBytes);
            // Cipher für Entschlüsselung vorbereiten
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // Rückumwandlung in Byte-Array
            encryptedBytes = java.util.Base64.getDecoder().decode(encryptedString);
            // Entschlüsselung
            utf8Bytes = cipher.doFinal(encryptedBytes);
            // Rückumwandlung in einen String
            System.out.println(encryptedString);
            System.out.println(new String(utf8Bytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            e.printStackTrace(System.out);
        }
        try {
            long start = System.currentTimeMillis();
            // Private/Public Pair erzeugen
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            // Verschlüsseln
            Cipher versch = Cipher.getInstance("RSA");
            versch.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] klartext = "Ein ganz tolles und sicheres Passwort#./0123456789-+,;:".getBytes();
            byte[] chiffrebytes = versch.doFinal(klartext);
            String chiffreText = new String(chiffrebytes);
            // Entschlüsseln
            Cipher entsch = Cipher.getInstance("RSA");
            entsch.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] entschBytes = entsch.doFinal(chiffrebytes);
            String entschText = new String(entschBytes);
            System.out.println(chiffreText);
            System.out.println(entschText);
            System.out.println("dauer (ms): " + (System.currentTimeMillis() - start));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            e.printStackTrace(System.out);
        }
    }
}
