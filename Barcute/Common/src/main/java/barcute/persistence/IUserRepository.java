package barcute.persistence;
import barcute.model.User;

public interface IUserRepository extends ICrudRepository<User> {
    boolean findOne(int id, String password);
}
