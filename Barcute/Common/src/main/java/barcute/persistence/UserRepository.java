package barcute.persistence;
import barcute.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserRepository implements IUserRepository {

    public UserRepository() {
        System.out.println("Creating user repo");
    }

    @Override
    public boolean findOne(int id, String password) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from User as u where u.id = ? and u.password = ?";
                List<User> user = session.createQuery(queryString, User.class)
                        .setParameter(0, id)
                        .setParameter(1, password)
                        .list();
                tx.commit();
                return user.size() == 1;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
