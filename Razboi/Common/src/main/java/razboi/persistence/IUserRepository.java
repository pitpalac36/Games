package razboi.persistence;

import razboi.model.User;

public interface IUserRepository extends ICrudRepository<User> {
    boolean findOne(String username, String password);
}
