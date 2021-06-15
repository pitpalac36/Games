package anagrame.repository;
import anagrame.domain.Dto;
import anagrame.domain.Ranking;
import anagrame.domain.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

// pt servicii rest da nu mai am chef :<
public class DtoRepository {

    public DtoRepository() {
        System.out.println("Creating dto repo");
    }

//    public List<Dto> getAll(String username) {
//        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
//            Transaction tx = null;
//            try {
//                tx = session.beginTransaction();
//                String queryString = "from Ranking as r where r.username = ?";
//                List<Ranking> rankings = session.createQuery(queryString, Ranking.class)
//                        .setParameter(0, username)
//                        .list();
//                tx.commit();
//                for (Ranking each : rankings) {
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
