package monopoly.model;
import javax.persistence.*;

@Entity
@Table(name="deals", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Deal {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gameId")
    private Game game;

    @Column(name="user")
    private String user;

    @Column(name="sum")
    private int sum;

    public Deal() {}

    public Deal(Game game, String user, int sum) {
        this.game = game;
        this.user = user;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "Deal{" +
                "id=" + id +
                ", game=" + game +
                ", user='" + user + '\'' +
                ", sum=" + sum +
                '}';
    }
}
