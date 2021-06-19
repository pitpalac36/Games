package game.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="games", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Game implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="inProgress")
    private boolean inProgress;

    @OneToMany(mappedBy = "game")
    @JsonIgnore
    private Set<Card> cards;

    public Game() {
        cards = new HashSet<>();
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card card) {
        cards.add(card);
        card.setGame(this);
    }
}
