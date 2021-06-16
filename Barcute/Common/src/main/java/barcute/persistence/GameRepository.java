package barcute.persistence;
import barcute.model.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class GameRepository implements IGameRepository {

    public GameRepository() {
        System.out.println("creating game repo");
    }

    @Override
    public synchronized Game getById(int id) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Game as g where g.id = ?";
                List<Game> game = session.createQuery(queryString, Game.class)
                        .setParameter(0, id)
                        .list();
                tx.commit();
                if (game.size() == 1)
                    return game.get(0);
                return null;
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
    public synchronized void create(Game g) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(g);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void update(Game g) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Game game = (Game) session.load(Game.class, g.getId());
                game.setPlayer1(g.getPlayer1());
                game.setPlayer2(g.getPlayer2());
                game.setPlace1player1(g.getPlace1player1());
                game.setPlace2player1(g.getPlace2player1());
                game.setPlace1player2(g.getPlace1player2());
                game.setPlace2player2(g.getPlace2player2());
                game.setAttemptsPlayer1(g.getAttemptsPlayer1());
                game.setAttemptsPlayer2(g.getAttemptsPlayer2());
                game.setWinner(g.getWinner());
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Game> getAll() {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Game as g";
                List<Game> games = session.createQuery(queryString, Game.class).list();
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

    @Override
    public List<Game> getAll(int id) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Game as g where g.player1=:player1 or g.player2=:player2";
                List<Game> games = session.createQuery(queryString, Game.class)
                        .setParameter("player1", id)
                        .setParameter("player2", id)
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
