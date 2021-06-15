package monopoly.persistence;

import monopoly.model.Deal;
import monopoly.model.Game;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DealRepository {

    public void create(Deal d) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(d);
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
}
