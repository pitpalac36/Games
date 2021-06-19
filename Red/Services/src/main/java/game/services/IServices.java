package game.services;
import game.model.Card;
import java.util.List;

public interface IServices {
    boolean login(String username, String password, IObserver client) throws Exception;
    void logout(String username, IObserver client) throws Exception;
    List<String> getAllPlayers() throws Exception;
    void addParticipant(String username, IObserver client) throws Exception;
    void sendCard(Card card, String username, IObserver client) throws Exception;
    void sendNumber(String username, IObserver client, int size) throws Exception;
}
