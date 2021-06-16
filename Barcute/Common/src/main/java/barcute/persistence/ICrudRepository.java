package barcute.persistence;
import java.util.List;

public interface ICrudRepository<T> {
    List<T> getAll();
}
