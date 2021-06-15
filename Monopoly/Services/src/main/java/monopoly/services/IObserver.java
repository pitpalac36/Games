package monopoly.services;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
    void newGame(int[] numbers, int sum) throws RemoteException;
    void pay(int cost, int position, String username) throws RemoteException;
    void receive(int currency, String username) throws RemoteException;
    void endGame() throws RemoteException;
    void wonOrNot(String currentWinner, int biggestScore) throws RemoteException;
}