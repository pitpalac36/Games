package barcute.services;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
    void startGame(int opponentId) throws RemoteException;
    void endGame(boolean won) throws RemoteException;
    void disableBoard() throws RemoteException;
    void enableBoard() throws RemoteException;
    void notifyGuess() throws RemoteException;
    void notifyDanger(int row, int column) throws RemoteException;
}
