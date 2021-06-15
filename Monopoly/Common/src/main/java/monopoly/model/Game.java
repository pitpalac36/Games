package monopoly.model;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="games", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Game {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="inProgress")
    private boolean inProgress;

    @OneToMany(mappedBy = "game")
    private Set<Deal> deals;

    public Game() {}

    public Game(boolean inProgress, Set<Deal> deals) {
        this.inProgress = inProgress;
        this.deals = deals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public Set<Deal> getDeals() {
        return deals;
    }

    public void setDeals(Set<Deal> deals) {
        this.deals = deals;
    }

    public void setDeal(Deal deal) {
        this.deals.add(deal);
        deal.setGame(this);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", inProgress=" + inProgress +
                ", deals=" + deals +
                '}';
    }
}
