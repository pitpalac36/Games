package monopoly.persistence;
import monopoly.model.Game;
import java.util.List;
import java.util.Map;

public interface IGameRepository {
    void create(Game g);
    Map<String, List<Integer>> getDeals(int gameId, String user);
    void update(Game g);
    Map<String, Integer> getRankings(int gameId);
}
