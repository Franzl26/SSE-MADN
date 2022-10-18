package ClientLogic;

import Dialogs.LoginPane;
import Dialogs.RoomSelectPane;
import RMIInterfaces.LoginInterface;
import RMIInterfaces.RaumauswahlInterface;
import Server.RaumauswahlObject;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Objects;

public class CommunicationWithServer {
    private static String username = null;
    private static RaumauswahlInterface roomSelect = null;
    private static UpdateRoomsObject updateRooms;
    private static RoomSelectPane roomSelectPane;

    /**
     * @param server   Server-IP zum Verbinden
     * @param username Benutzername
     * @param password Passwort
     * @return -2 Server nicht erreichbar, -1 Login-Daten fehlerhaft, 1 Login erfolgreich
     */
    public static int tryToLogin(String server, String username, String password) {
        try {
            String loginString = "//" + server + "/" + "MADNLogin";
            LoginInterface login = (LoginInterface) Naming.lookup(loginString);
            PublicKey publicKey = login.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] chiffrat = cipher.doFinal(password.getBytes());
            roomSelect = login.login(username, chiffrat);
            if (roomSelect == null) {
                return -1;
            }

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace(System.out);
            return -2;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("Verschlüsselung nicht möglich");
        }
        CommunicationWithServer.username = username;
        RoomSelectPane.RoomSelectPaneStart(username);
        return 1;
    }

    /**
     * @param server   Server-IP
     * @param username Benutzername
     * @param pw1      Passwort
     * @param pw2      Passwort wiederholt
     * @return -1 Passwörter stimmen nicht überein, -2 Passwort entspricht nicht den Richtlinien,
     * -3 Benutzername entspricht nicht den Richtlinien, -4 Server nicht erreichbar, -5 Benutzername bereits vorhanden
     * 1 Registrierung erfolgreich
     */
    public static int tryToRegister(String server, String username, String pw1, String pw2) {
        if (!Objects.equals(pw1,pw2)) return -1;
        if (!DataAndMethods.MiscMethods.checkPasswordGuidelines(pw1)) return -2;
        if (!DataAndMethods.MiscMethods.checkUsernameGuidelines(username)) return -3;
        try {
            String loginString = "//" + server + "/" + "MADNLogin";
            LoginInterface login = (LoginInterface) Naming.lookup(loginString);
            PublicKey publicKey = login.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] chiffrat = cipher.doFinal(pw1.getBytes());
            int ret = login.register(username, chiffrat);
            if (ret == -1) {
                return -2;
            } else if (ret == -2) {
                return -3;
            } else if (ret == -3) {
                return -5;
            }

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace(System.out);
            return -4;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("Verschlüsselung nicht möglich");
        }

        LoginPane.LoginPaneStart();
        return 1;
    }

    public static void subscribeUpdateRooms(RoomSelectPane pane) {
        try {
            updateRooms = new UpdateRoomsObject(pane);
            roomSelect.subscribeToRoomUpdates(updateRooms);
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            ((Stage)pane.getScene().getWindow()).close();
        }
        roomSelectPane = pane;
    }

    public static void unsubscribeUpdateRooms() {
        try {
            roomSelect.unsubscribeFromRoomUpdates(updateRooms);
            roomSelectPane = null;
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            ((Stage)roomSelectPane.getScene().getWindow()).close();
        }
    }

    public static void createNewRoom() {

    }
}
