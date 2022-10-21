package Dialogs;

import DataAndMethods.BoardState;
import RMIInterfaces.UpdateGameInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UpdateGameObject extends UnicastRemoteObject implements UpdateGameInterface {
    private final GameLogic logic;

    protected UpdateGameObject(GameLogic gameLogic) throws RemoteException {
        logic = gameLogic;
    }

    @Override
    public void displayNewState(BoardState state, int[] changed, String[] names, int turn) throws RemoteException {
        logic.displayNewState(state, changed,names,turn);
    }

    @Override
    public void displayDice(int number) throws RemoteException {
        logic.displayDice(number);
    }

    @Override
    public void rollDiceOver(int wurf) throws RemoteException {
        logic.rollDiceOver(wurf);
    }

    @Override
    public void timesRunning() throws RemoteException {
        logic.displayGif();
    }

    @Override
    public void moveFigureOver() throws RemoteException {
        logic.moveFigureOver();
    }
}
