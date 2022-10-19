package ClientLogic;

import DataAndMethods.BoardConfiguration;
import DataAndMethods.BoardConfigurationBytes;
import DataAndMethods.Room;
import Dialogs.DesignPane;
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
        try {

            LoginInterface login = (LoginInterface) Naming.lookup("//" + server + "/" + "MADNLogin");
            PublicKey publicKey = login.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] chiffrat = cipher.doFinal(password.getBytes());
            roomSelect = login.login(username, chiffrat, pane.getURI());
            if (roomSelect == null) {
                ((Stage) pane.getScene().getWindow()).close();
                return -1;
            }

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace(System.out);
            ((Stage) pane.getScene().getWindow()).close();
            return -2;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("Verschlüsselung nicht möglich");
        }
        CommunicationWithServer.username = username;
        ((Stage) pane.getScene().getWindow()).show();
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
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }

    /**
     * @return -1 max Raumanzahl erreicht, 1 erfolgreich
     */
    public static int createNewRoom(UpdateRoomsInterface uri) {
        LobbyPane pane = LobbyPane.LobbyPaneStart();

        try {
            LobbyInterface lobby = roomSelect.createNewRoom(uri, pane.getULI());
            if (lobby == null) {
                ((Stage) pane.getScene().getWindow()).close();
                return -1;
            }
            lobbyInterface = lobby;
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        ((Stage) pane.getScene().getWindow()).show();
        return 1;
    }

    /**
     * @return -1 Raum voll, 1 erfolgreich
     */
    public static int enterRoom(UpdateRoomsInterface uri, Room room) {
        LobbyPane pane = LobbyPane.LobbyPaneStart();
        try {
            LobbyInterface lobby = roomSelect.enterRoom(uri, room, pane.getULI());
            if (lobby == null) {
                ((Stage) pane.getScene().getWindow()).close();
                return -1;
            }
            lobbyInterface = lobby;
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        ((Stage) pane.getScene().getWindow()).show();
        return 1;
    }

    /**
     * @return -1 schon 4 Bot vorhanden, 1 erfolgreich
     */
    public static int addBot(UpdateLobbyInterface uli) {
        try {
            return lobbyInterface.addBot(uli);
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        System.err.println("Test"); // todo entfernen
        return -1;
    }

    /**
     * @return -1 kein Bot mehr da, 1 erfolgreich
     */
    public static int removeBot(UpdateLobbyInterface uli) {
        try {
            return lobbyInterface.removeBot(uli);
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        System.err.println("Test"); // todo entfernen
        return -1;
    }

    /**
     * @return -1 nicht genug Spieler, 1 erfolgreich
     */
    public static int spielStarten(UpdateLobbyInterface uli) {
        return -1;
    }

    public static void raumVerlassen(UpdateLobbyInterface uli) {
        RoomSelectPane pane = RoomSelectPane.RoomSelectPaneStart(username);
        try {
            lobbyInterface.raumVerlassen(uli);
            roomSelect.subscribeToRoomUpdates(pane.getURI(),username);
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        lobbyInterface = null;
        ((Stage) pane.getScene().getWindow()).show();
    }

    /**
     * @return -1 Design wird schon angepasst, 1 design kann angepasst werden
     */
    public static int designAnpassen(UpdateLobbyInterface uli) {
        try {
            int ret = lobbyInterface.designAnpassen(uli);
            if (ret == -1) return -1;
            DesignPane pane = DesignPane.DesignPaneStart(lobbyInterface.getDesignsList(uli), uli);

        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }

        return 1;
    }

    public static BoardConfigurationBytes getBoardConfig(UpdateLobbyInterface uli, String design) {
        try {
            return lobbyInterface.getBoardConfig(uli,design);
        } catch (RemoteException e) {
            e.printStackTrace(System.out);
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        return null;
    }

    public static void designBestaetigen(UpdateLobbyInterface uli, String design) {
        try {
            lobbyInterface.designBestaetigen(uli,design);
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }

    public static void designAendernAbbrechen() {
        try {
            lobbyInterface.designAendernAbbrechen();
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }
}
