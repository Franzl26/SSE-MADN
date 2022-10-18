package ClientLogic;

import DataAndMethods.Room;
import Dialogs.LobbyPane;
import Dialogs.LoginPane;
import Dialogs.RoomSelectPane;
import RMIInterfaces.*;
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
    private static String username;
    private static RaumauswahlInterface roomSelect;
    private static LobbyInterface lobbyInterface;

    /**
     * @return -2 Server nicht erreichbar, -1 Login-Daten fehlerhaft, 1 Login erfolgreich
     */
    public static int tryToLogin(String server, String username, String password) {
        RoomSelectPane pane = RoomSelectPane.RoomSelectPaneStart(username);
        UpdateRoomsInterface uri = pane.getURI();
        try {

            LoginInterface login = (LoginInterface) Naming.lookup("//" + server + "/" + "MADNLogin");
            PublicKey publicKey = login.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] chiffrat = cipher.doFinal(password.getBytes());
            roomSelect = login.login(username, chiffrat, uri);
            if (roomSelect == null) {
                ((Stage)pane.getScene().getWindow()).close();
                return -1;
            }

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace(System.out);
            ((Stage)pane.getScene().getWindow()).close();
            return -2;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("Verschlüsselung nicht möglich");
        }

        ((Stage)pane.getScene().getWindow()).show();
        return 1;
    }

    /**
     * @return -1 Passwörter stimmen nicht überein, -2 Passwort entspricht nicht den Richtlinien,
     * -3 Benutzername entspricht nicht den Richtlinien, -4 Server nicht erreichbar, -5 Benutzername bereits vorhanden
     * 1 Registrierung erfolgreich
     */
    public static int tryToRegister(String server, String username, String pw1, String pw2) {
        if (!Objects.equals(pw1, pw2)) return -1;
        if (!DataAndMethods.MiscMethods.checkPasswordGuidelines(pw1)) return -2;
        if (!DataAndMethods.MiscMethods.checkUsernameGuidelines(username)) return -3;
        try {
            LoginInterface login = (LoginInterface) Naming.lookup("//" + server + "/" + "MADNLogin");
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

    public static void unsubscribeUpdateRooms(UpdateRoomsInterface uri) {
        try {
            roomSelect.unsubscribeFromRoomUpdates(uri);
            roomSelect = null;
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }

    /**
     * @return -1 max Raumanzahl erreicht, 1 erfolgreich
     */
    public static int createNewRoom() {
        LobbyPane pane = LobbyPane.LobbyPaneStart();
        UpdateLobbyInterface uli = pane.getULI();

        try {
            LobbyInterface lobby = roomSelect.createNewRoom(username, uli);
            if (lobby == null) {
                ((Stage)pane.getScene().getWindow()).close();
                return -1;
            }
            lobbyInterface = lobby;
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        roomSelect = null;
        ((Stage)pane.getScene().getWindow()).show();
        return 1;
    }

    /**
     * @return -1 Raum voll, 1 erfolgreich
     */
    public static int enterRoom(Room room) {
        LobbyPane pane = LobbyPane.LobbyPaneStart();
        UpdateLobbyInterface uli = pane.getULI();
        try {
            LobbyInterface lobby = roomSelect.enterRoom(username, room, uli);
            if (lobby == null) {
                ((Stage)pane.getScene().getWindow()).close();
                return -1;
            }
            lobbyInterface = lobby;
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        roomSelect = null;
        ((Stage)pane.getScene().getWindow()).show();
        return 1;
    }
}
