package barcute.persistence;
import barcute.model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PlaceRepository {

    public PlaceRepository() {
        System.out.println("Creating place repo");
    }

    public synchronized void create(Place p) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(p);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
