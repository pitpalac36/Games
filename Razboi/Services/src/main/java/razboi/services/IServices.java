package razboi.services;
import java.util.List;

public interface IServices {
    boolean login(String username, String password, IObserver client) throws Exception;
    void logout(String username, IObserver client) throws Exception;
    void addParticipant(String username, IObserver client);
    List<String> getAllPlayers();
    void sendCard(String card, String username, IObserver client);
}
