package anagrame.services;
import anagrame.domain.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
    void enableStart() throws RemoteException;
    void disableStart() throws RemoteException;
    void newRound(Integer id, String word, Integer points) throws RemoteException;
    void endGame(String correctWord, int points) throws RemoteException;
    void setGame(Game currentGame) throws RemoteException;;
    void resultReceived(String correctWord, int points) throws RemoteException;
    void wonOrNot(int finalScore, int biggestScore) throws RemoteException;
}
