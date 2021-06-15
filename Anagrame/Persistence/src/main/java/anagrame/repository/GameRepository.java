package anagrame.repository;
import anagrame.domain.Game;
import anagrame.domain.Ranking;
import anagrame.domain.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class GameRepository implements IGameRepository {

    public GameRepository() {
        System.out.println("Creating game repo");
    }

    @Override
    public void create(Game g) {
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
                game.setCurrentWord(g.getCurrentWord());
                game.setInProgress(g.getInProgress());
                game.getRankings().addAll(g.getRankings());
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
    public int getPoints(String user, int gameId, int round) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Ranking as r where r.username = ? and r.round=?";
                List<Ranking> rankings = session.createQuery(queryString, Ranking.class)
                        .setParameter(0, user)
                        .setParameter(1, round)
                        .list();
                for (Ranking r : rankings)
                    if (r.getGame().getId() == gameId)
                        return r.getPoints();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    @Override
    public int getFinalScore(String user, Game game) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Ranking as r where r.username = ? and r.game=?";
                List<Ranking> rankings = session.createQuery(queryString, Ranking.class)
                        .setParameter(0, user)
                        .setParameter(1, game)
                        .list();
                int result = 0;
                for (Ranking r : rankings)
                    result += r.getPoints();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
