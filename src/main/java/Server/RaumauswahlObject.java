package Server;

import RMIInterfaces.RaumauswahlInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RaumauswahlObject extends UnicastRemoteObject implements RaumauswahlInterface {
    protected RaumauswahlObject() throws RemoteException {
    }
}
