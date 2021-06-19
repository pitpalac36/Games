package game.persistence;
import game.model.Card;
import game.model.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class CardRepository implements ICardRepository {
    @Override
    public void create(Card c) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(c);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void update(Card c) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Card card = session.load(Card.class, c.getId());
                card.setRound(c.getRound());
                card.setGame(c.getGame());
                card.setFromServer(c.isFromServer());
                card.setWon(c.isWon());
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<Card> getCards(Game game, int round) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Card as c where c.game=? and c.round=?";
                List<Card> cards = session.createQuery(queryString, Card.class)
                        .setParameter(0, game)
                        .setParameter(1, round)
                        .list();
                tx.commit();
                return cards;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Card> getCardsFromServer(Game game, String username) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Card as c where c.game=? and c.username=?";
                List<Card> cards = session.createQuery(queryString, Card.class)
                        .setParameter(0, game)
                        .setParameter(1, username)
                        .list();
                tx.commit();
                return cards;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Card> getAll() {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Card as c";
                List<Card> cards = session.createQuery(queryString, Card.class)
                        .list();
                tx.commit();
                return cards;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
