package razboi.services;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IObserver extends Remote {
    void newGame(String[] currentRoundCards) throws RemoteException;
    void winCards(List<String> keySet) throws RemoteException;
    void disableCards() throws RemoteException;
    void enableCards() throws RemoteException;
}
