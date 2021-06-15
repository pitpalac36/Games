package anagrame.domain;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="games", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Game implements Serializable {
    @Id
    @Column(name="id")
    public int id;

    @Column(name="currentWord")
    private String currentWord;

    @OneToMany(mappedBy = "game")
    private Set<Ranking> rankings;

    @Column(name="inProgress")
    private boolean inProgress;

    public Game() {
    }

    public Game(int id, String currentWord, Set<Ranking> rankings, boolean inProgress) {
        this.id = id;
        this.currentWord = currentWord;
        this.rankings = rankings;
        this.inProgress = inProgress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public Set<Ranking> getRankings() {
        return rankings;
    }

    public void setRanking(Ranking ranking) {
        this.rankings.add(ranking);
        ranking.setGame(this);
    }

    public void addChild(Ranking ranking) {
        this.rankings.add(ranking);
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", currentWord='" + currentWord + '\'' +
                ", rankings=" + rankings +
                ", inProgress=" + inProgress +
                '}';
    }
}
