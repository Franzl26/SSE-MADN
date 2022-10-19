package Server;

import javafx.embed.swing.JFXPanel;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerStart {
    public static void main(String[] args) {
        new JFXPanel();
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            LoginObject login = new LoginObject();
            Naming.rebind("//localhost/MADNLogin",login);
            System.out.println("Server started");

        } catch (RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
