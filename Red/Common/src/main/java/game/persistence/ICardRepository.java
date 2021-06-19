package game.persistence;
import game.model.Card;
import game.model.Game;
import java.util.List;

public interface ICardRepository extends ICrudRepository<Card> {
    void create(Card c);
    void update(Card c);
    List<Card> getCards(Game game, int round);
    List<Card> getCardsFromServer(Game game, String username);
}
