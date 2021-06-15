package anagrame.services;

import anagrame.domain.User;

import java.rmi.RemoteException;
import java.util.List;

public interface IServices {
    boolean login(String username, String password, IObserver client) throws Exception;
    void logout(String username, IObserver client) throws Exception;
    void startGame() throws Exception;
    void sendResponse(String username, Integer gameId, String word) throws Exception;
    List getAllPlayers();
    void requestRound() throws RemoteException;
    void announceEnded(User user, IObserver client);
}
