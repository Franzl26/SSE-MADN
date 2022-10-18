package Server;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class CredentialsSave implements Serializable {
    private static final long REMOVE_USERS_AFTER = 604800000; // todo Alte Benutzer löschen nach

    private final HashMap<String, User> users = new HashMap<>();

    /**
     * @return -1 Nutzer bereits vorhanden, 1 erfolgreich
     */
    public synchronized int addUser(String username, byte[] password) {
        if (users.containsKey(username)) return -1;
        users.put(username, new User(password));
        return 1;
    }

    /**
     * @return -1 Benutzername nicht vorhanden, -2 Passwort falsch, 1 erfolgreich
     */
    public synchronized int checkPassword(String username, byte[] password) {
        User user = users.get(username);
        if (user == null) return -1;
        if (!Arrays.equals(user.passwordHash, password)) return -2;
        user.lastLogin = System.currentTimeMillis();
        return 1;
    }

    public synchronized void saveCredentials() {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("./resources/Server/credentials"))) {
            os.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CredentialsSave loadCredentials() {
        File file = new File("./resources/Server/credentials");
        if (!file.isFile()) {
            System.err.println("Keine gespeicherten Benutzer gefunden");
            return null;
        }
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(file))) {
            return (CredentialsSave) os.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Credentials konnten nicht gelesen werden");
        }
        return null;
    }

    public synchronized void deleteOldUsers() {
        long timeNow = System.currentTimeMillis();
        for (String name : users.keySet()) {
            User u = users.get(name);
            if (u.lastLogin + REMOVE_USERS_AFTER < timeNow) users.remove(name);
        }
    }

    private static class User implements Serializable {
        private final byte[] passwordHash;
        private long lastLogin;

        private User(byte[] passwordHash) {
            this.passwordHash = passwordHash;
            lastLogin = System.currentTimeMillis();
        }
    }
}
