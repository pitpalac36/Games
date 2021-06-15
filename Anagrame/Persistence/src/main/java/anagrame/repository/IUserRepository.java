package anagrame.repository;
import anagrame.domain.User;
import java.util.List;

public interface IUserRepository {
    boolean findOne(String username, String password);
    List getAll();
}
