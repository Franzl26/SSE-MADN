package Server;

import RMIInterfaces.LoginInterface;
import RMIInterfaces.RaumauswahlInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Timer;
import java.util.TimerTask;

public class LoginObject extends UnicastRemoteObject implements LoginInterface {
    private KeyPair keys;

    protected LoginObject() throws RemoteException {
        Timer keyTimer = new Timer("keyPairGenTimer");
        keyTimer.schedule(new GenKeyPairs(),0,3600000); // 1h

    }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return keys.getPublic();
    }

    /**
     *
     * @return null bei fehlerhaftem Login, sonst Objekt
     */
    @Override
    public RaumauswahlInterface login(String username, byte[] password) throws RemoteException {
        return null;
    }

    @Override
    public int register(String username, byte[] password) throws RemoteException {
        return -1;
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
}
