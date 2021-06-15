package monopoly.persistence;
import monopoly.model.Deal;
import monopoly.model.Game;
import monopoly.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRepository implements IGameRepository {

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
                game.setInProgress(g.getInProgress());
                game.getDeals().addAll(g.getDeals());
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

    private Game getById(int gameId) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Game as g where g.id=?";
                List<Game> game = session.createQuery(queryString, Game.class)
                        .setParameter(0, gameId)
                        .list();
                tx.commit();
                if (game.size() == 1)
                    return game.get(0);
                else return null;
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
    public Map<String, List<Integer>> getDeals(int gameId, String user) {
        System.out.println(JdbcUtils.getSessionFactory());
        Game g = getById(gameId);
        if (g == null) return null;
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Deal as d where d.game = ? and d.user = ?";
                List<Deal> deals = session.createQuery(queryString, Deal.class)
                        .setParameter(0, g)
                        .setParameter(1, user)
                        .list();
                tx.commit();
                Map<String, List<Integer>> result = new HashMap<>();
                List<Integer> paid = new ArrayList<>();
                List<Integer> received = new ArrayList<>();
                for (Deal each : deals) {
                    if (each.getSum() < 0) // paid
                       paid.add((-1) * each.getSum());
                    else received.add(each.getSum());
                }
                result.put("paid", paid);
                result.put("received", received);
                return result;
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
    public Map<String, Integer> getRankings(int gameId) {
        System.out.println(JdbcUtils.getSessionFactory());
        Game g = getById(gameId);
        if (g == null) return null;
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from Deal as d where d.game = ?";
                List<Deal> deals = session.createQuery(queryString, Deal.class)
                        .setParameter(0, g)
                        .list();
                tx.commit();
                Map<String, Integer> result = new HashMap<>();

                int newSum = 0;
                for (Deal each : deals) {
                    if (result.get(each.getUser()) != null) {
                        newSum = result.get(each.getUser()) + each.getSum();
                    } else {
                        newSum = each.getSum();
                    }
                    result.put(each.getUser(), newSum);
                }
                return result;
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
