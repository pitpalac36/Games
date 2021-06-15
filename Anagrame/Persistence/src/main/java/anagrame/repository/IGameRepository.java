package anagrame.repository;

import anagrame.domain.Game;

public interface IGameRepository {
    void create(Game g);
    void update(Game g);
    public int getPoints(String user, int gameId, int round);
    public int getFinalScore(String user, Game game);
}
