package razboi.persistence;
import java.util.List;

public interface ICrudRepository<E> {
    void save(E entity);
    List<E> getAll();
}
