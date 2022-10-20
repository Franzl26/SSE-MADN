package Server;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerStart extends Application {
    public static void main(String[] args) {
        //new JFXPanel();
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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
