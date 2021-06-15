package anagrame.domain;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="rankings", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Ranking implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="username")
    private String username;

    @Column(name="points")
    private int points;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gameId")
    private Game game;

    @Column(name="round")
    private int round;

    public Ranking() {
    }

    public Ranking(String username, int points, Game game, int round) {
        this.username = username;
        this.points = points;
        this.game = game;
        this.round = round;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        setChild(game);
    }

    private void setChild(Game game) {
        game.addChild(this);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", points=" + points +
                ", game=" + game +
                ", round=" + round +
                '}';
    }
}
