package RMIInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

public interface LoginInterface extends Remote {
    /**
     *
     * @return publicKey für RSA-Verschlüsselung
     */
    PublicKey getPublicKey() throws RemoteException;

    /**
     *
     * @return null bei fehlerhaftem Login, sonst Objekt
     */
    RaumauswahlInterface login(String username, byte[] password) throws RemoteException;

    /**
     * @return -1 Passwort entspricht nicht den Richtlinien, -2 Benutzername entspricht nicht den Richtlinien,
     * 1 Registrierung erfolgreich
     */
    int register(String username, byte[] password) throws RemoteException;
}
