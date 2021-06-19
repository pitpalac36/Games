package game.persistence;

import game.model.Game;

public interface IGameRepository extends ICrudRepository<Game> {
    void save(Game g);
    void update(Game g);
    Game findOne(int gameId);
}
