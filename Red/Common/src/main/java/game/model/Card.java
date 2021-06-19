package game.model;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="cards", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gameId")
    private Game game;

    @Column(name="username")
    private String username;

    @Column(name="value")
    private String value;

    @Column(name="fromServer")
    private boolean fromServer;

    @Column(name="won")
    private boolean won;

    @Column(name="round")
    private int round;

    public Card() {
    }

    public Card(String username, String value, boolean fromServer, boolean won, int round) {
        this.username = username;
        this.value = value;
        this.fromServer = fromServer;
        this.won = won;
        this.round = round;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFromServer() {
        return fromServer;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", game=" + game +
                ", username='" + username + '\'' +
                ", value='" + value + '\'' +
                ", fromServer=" + fromServer +
                ", won=" + won +
                ", round=" + round +
                '}';
    }

    public int getGameId() {
        return game.getId();
    }

    public void setGameId(int gameId) {
        game.setId(gameId);
    }
}
