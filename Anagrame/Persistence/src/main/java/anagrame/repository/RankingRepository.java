package anagrame.repository;
import anagrame.domain.Ranking;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RankingRepository {

    public RankingRepository() {
        System.out.println("Creating ranking repo");
    }

    public void create(Ranking r) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(r);
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
