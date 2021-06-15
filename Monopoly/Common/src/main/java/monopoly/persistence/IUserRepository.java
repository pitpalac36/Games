package monopoly.persistence;

public interface IUserRepository {
    boolean findOne(String username, String password);
}
