package anagrame.repository;
import anagrame.domain.Word;
import org.hibernate.Session;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class WordRepository implements IWordRepository {

    public WordRepository() {
        System.out.println("Creating word repo");
    }

    @Override
    public Word chooseRandom() {
        try (Session session = JdbcUtils.getSessionFactory().openSession()) {
            CriteriaQuery<Word> criteriaQuery = session.getCriteriaBuilder().createQuery(Word.class);
            criteriaQuery.from(Word.class);
            List<Word> words = session.createQuery(criteriaQuery).getResultList();
            int index = ThreadLocalRandom.current().nextInt(0, words.size());
            return words.get(index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
