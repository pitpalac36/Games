package barcute.persistence;
import barcute.model.Game;
import java.util.List;

public interface IGameRepository extends ICrudRepository<Game> {
    Game getById(int id);
    void create(Game g);
    void update(Game g);
    List<Game> getAll(int userId);
}
