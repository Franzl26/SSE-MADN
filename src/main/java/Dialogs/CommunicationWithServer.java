package Dialogs;

import DataAndMethods.BoardConfigurationBytes;
import DataAndMethods.Room;
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
    private static LoggedInInterface lii;

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
            lii = login.login(username, chiffrat, pane.getURI());
            if (lii == null) {
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

    public static void logout() {
        try {
            lii.logOut();
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }

    public static void unsubscribeUpdateRooms() {
        try {
            lii.unsubscribeFromRoomUpdates();
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

        try {
            int ret = lii.createNewRoom(new UpdateLobbyObject(pane));
            if (ret == -1) {
                ((Stage) pane.getScene().getWindow()).close();
                return -1;
            }
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
    public static int enterRoom(Room room) {
        LobbyPane pane = LobbyPane.LobbyPaneStart();
        try {
            int ret = lii.enterRoom(room, new UpdateLobbyObject(pane));
            if (ret == -1) {
                ((Stage) pane.getScene().getWindow()).close();
                return -1;
            }
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
    public static int addBot() {
        try {
            return lii.addBot();
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        return -1;
    }

    /**
     * @return -1 kein Bot mehr da, 1 erfolgreich
     */
    public static int removeBot() {
        try {
            return lii.removeBot();
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        return -1;
    }

    /**
     * @return -1 nicht genug Spieler, 1 erfolgreich
     */
    public static int spielStarten() {
        /*GameLogic gameLogic = new GameLogic();
        try {
            int ret = lii.spielStarten(gameLogic.getUGI());
            if (ret == -1) {
                gameLogic.closePane();
                return -1;
            }
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        gameLogic.showPane();*/
        return -1;
    }

    public static void raumVerlassen() {
        try {
            RoomSelectPane pane = RoomSelectPane.RoomSelectPaneStart(lii.getUsername());
            lii.raumVerlassen();
            lii.subscribeToRoomUpdates(pane.getURI());
            ((Stage) pane.getScene().getWindow()).show();
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }

    public static void designAnpassen() {
        try {
            DesignPane.DesignPaneStart(lii.getDesignsList());
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }

    public static void designBestaetigen(String design) {
        try {
            lii.designBestaetigen(design);
        } catch (RemoteException e) {
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
    }

    public static BoardConfigurationBytes getBoardConfig(String design) {
        try {
            return lii.getBoardConfig(design);
        } catch (RemoteException e) {
            e.printStackTrace(System.out);
            new Alert(Alert.AlertType.INFORMATION, "Kommunikation mit Server abgebrochen, beende Spiel").showAndWait();
            System.exit(0);
        }
        return null;
    }
}
