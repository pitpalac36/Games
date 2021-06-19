package game.services;
import game.model.Card;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IObserver extends Remote {
    void startGame(List<String> entrySet, Card c1, Card c2, Card c3) throws RemoteException;
    void disable() throws RemoteException;
    void enable() throws RemoteException;
    void wonCards(List<Card> thisRound) throws RemoteException;
    void endGame() throws RemoteException;
}