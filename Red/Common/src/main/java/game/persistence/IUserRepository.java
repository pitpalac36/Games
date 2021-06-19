package game.persistence;
import game.model.User;

public interface IUserRepository extends ICrudRepository<User> {
    boolean findOne(String username, String password);
    User findOne(String username);
}
