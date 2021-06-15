package monopoly.services;
import java.util.List;

public interface IServices {
    boolean login(String username, String password, IObserver client) throws Exception;
    void logout(String username, IObserver client) throws Exception;
    void addParticipant(String username, IObserver client) throws Exception;
    void movePawn(String username, int currentPosition, int generated, int currency, IObserver client) throws Exception;
    List<String> getAllPlayers();
    void announceFinish(IObserver client);
    void sendScore(int currency, String username, IObserver client);
}