package App;

import Dialogs.GameLogic;
import DataAndMethods.BoardConfiguration;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public class Start extends Application {
    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);


        //LoginPane.LoginPaneStart();
        //RegisterPane.RegisterPaneStart();
        //RoomSelectPane.RoomSelectPaneStart("Username");
        //LobbyPane.LobbyPaneStart();
        //DesignPane.designsTest();
        //GameLogic.testBoardLogic();
        //GameStatisticsPane.GameStatisticsPaneStart();

        //new Alert(Alert.AlertType.INFORMATION, "<Text>").show();
        //new Alert(Alert.AlertType.CONFIRMATION, "<Text>").show();

    }

    public static void main(String[] args) {
        //hash();
        //boardConfigTest();
        //kryptoTest();
        //timerTest();
        //vectorTest();
        //configTest();
        //folderTest();
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
        return new String(hexChars);
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

    public static void timerTest() {
        Task task = new Task();
        Timer timer = new Timer("Zeitausgabe");
        timer.scheduleAtFixedRate(task,2500,2000);
        System.out.println(System.currentTimeMillis());
    }

    public static class Task extends TimerTask {
        @Override
        public void run() {
            System.out.println(System.currentTimeMillis());
        }
    }

    public static void vectorTest() {
        Vector<String> vector = new Vector<>(25);
        System.out.println(vector.size());
        vector.add("e");
        System.out.println(vector.size());
    }

    public static void configTest() {
        BoardConfiguration config = BoardConfiguration.loadBoardKonfiguration("./resources/designs/Standard/");
        BoardConfiguration config2 = BoardConfiguration.loadBoardKonfiguration("./resources/test/");
        System.out.println("funktioniert");
    }

    public static void folderTest() {
        File f = new File("./resources/designs/Standard/pathNormal.png");
        File f2 = new File("./resources/test/pathOut.png");
        try (DataInputStream dis = new DataInputStream(new FileInputStream(f)); DataOutputStream dos = new DataOutputStream(new FileOutputStream(f2))) {
            int length = (int) f.length();
            System.out.println(length);
            byte[] pic = new byte[length];
            dis.readFully(pic);
            dos.write(pic);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
