package Dialogs;

import DataAndMethods.BoardConfiguration;
import RMIInterfaces.UpdateDesignInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UpdateDesignObject extends UnicastRemoteObject implements UpdateDesignInterface {
    protected UpdateDesignObject() throws RemoteException {
    }

    @Override
    public void updateDesign(BoardConfiguration config) throws RemoteException {

    }
}
