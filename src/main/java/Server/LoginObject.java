package Server;

import DataAndMethods.MiscMethods;
import RMIInterfaces.LoginInterface;
import RMIInterfaces.RaumauswahlInterface;
import RMIInterfaces.UpdateRoomsInterface;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class LoginObject extends UnicastRemoteObject implements LoginInterface {
    private KeyPair keys;
    private final RaumauswahlObject raumauswahl;
    private CredentialsSave users;
    private HashSet<String> isLoggedIn = new HashSet<>();

    protected LoginObject() throws RemoteException {
        Timer keyTimer = new Timer("keyPairGenTimer");
        keyTimer.schedule(new GenKeyPairs(), 0, 3600000); // 0, 1h
        Timer oldUsersTimer = new Timer("deleteOldUsersTimer");
        oldUsersTimer.schedule(new DeleteOldUsers(),3600000, 86400000); // 1h, 1d

        users = CredentialsSave.loadCredentials();
        if (users == null) users = new CredentialsSave();
        raumauswahl = new RaumauswahlObject();
    }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return keys.getPublic();
    }

    @Override
    public RaumauswahlInterface login(String username, byte[] password, UpdateRoomsInterface uri) throws RemoteException {
        byte[] pwDecrypted = hashPassword(decryptPassword(password));
        if (users.checkPassword(username, pwDecrypted) != 1) return null;
        if (isLoggedIn.contains(username)) return null;
        isLoggedIn.add(username);
        raumauswahl.addClient(uri,username);
        users.saveCredentials();
        return raumauswahl;
    }

    @Override
    public void logout(String username, UpdateRoomsInterface uri) throws RemoteException {
        isLoggedIn.remove(username);
        raumauswahl.unsubscribeFromRoomUpdates(uri);
    }

    @Override
    public int register(String username, byte[] password) throws RemoteException {
        if (!MiscMethods.checkUsernameGuidelines(username)) return -2;
        byte[] pwDecrypted = decryptPassword(password);
        if (!MiscMethods.checkPasswordGuidelines(new String(pwDecrypted))) return -1;
        byte[] pwHashed = hashPassword(pwDecrypted);
        if (users.addUser(username, pwHashed) == -1) return -3;
        users.saveCredentials();
        return 1;
    }

    private byte[] decryptPassword(byte[] pw) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keys.getPrivate());
            return cipher.doFinal(pw);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("Passwort entschlüsseln nicht möglich");
        }
    }

    private static byte[] hashPassword(byte[] pw) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Passwort-Hash kann nicht gebildet werden");
        }
        md.update(pw);
        return md.digest();
    }

    private class GenKeyPairs extends TimerTask {
        @Override
        public void run() {
            try {
                keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                if (keys == null) throw new RuntimeException("Schlüsselpaar konnte nicht erstellt werden");
                else System.out.println("Neues Schlüsselpaar konnte nicht erstellt werden");
            }
        }
    }

    private class DeleteOldUsers extends TimerTask {

        @Override
        public void run() {
            users.deleteOldUsers();
        }
    }
}
