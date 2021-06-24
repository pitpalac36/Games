package game.persistence;

import game.model.Game;
import game.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class GameRepository implements IGameRepository {

    public GameRepository() {
    }

    @Override
    public void save(Game g) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(g);
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
    public synchronized void update(Game g) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Game game = (Game) session.load(Game.class, g.getId());
                game.setCards(g.getCards());
                game.setInProgress(g.isInProgress());
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
    public Game findOne(int gameId) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Game as g where g.id = ?";
                Game game = session.createQuery(queryString, Game.class)
                        .setParameter(0, gameId)
                        .uniqueResult();
                tx.commit();
                return game;
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
    public List<Game> getAll() {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Game as g";
                List<Game> games = session.createQuery(queryString, Game.class)
                        .list();
                tx.commit();
                return games;
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
