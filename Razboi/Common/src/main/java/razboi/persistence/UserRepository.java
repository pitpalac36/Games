package razboi.persistence;
import org.hibernate.Session;
import org.hibernate.Transaction;
import razboi.model.User;
import java.util.List;

public class UserRepository implements IUserRepository {

    public UserRepository() {
        System.out.println("Creating user repo");
    }

    @Override
    public boolean findOne(String username, String password) {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String queryString = "from User as u where u.username = ? and u.password = ?";
                List<User> user = session.createQuery(queryString, User.class)
                        .setParameter(0, username)
                        .setParameter(1, password)
                        .list();
                tx.commit();
                if (user.size() == 1)
                    return true;
                else return false;
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
    public void save(User entity) {
        //
    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
