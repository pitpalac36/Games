package anagrame.domain;
import javax.persistence.*;

@Entity
@Table(name="words", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Word {
    @Id
    @Column(name="id")
    private int id;

    @Column(name="word")
    private String word;

    public Word() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                '}';
    }
}
