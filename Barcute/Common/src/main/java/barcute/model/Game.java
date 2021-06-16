package barcute.model;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="games", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Game implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="player1")
    private int player1;

    @Column(name="player2")
    private int player2;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="place1player1", referencedColumnName="placeId")
    private Place place1player1;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="place2player1", referencedColumnName="placeId")
    private Place place2player1;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="place1player2", referencedColumnName="placeId")
    private Place place1player2;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="place2player2", referencedColumnName="placeId")
    private Place place2player2;

    @Column(name="attemptsPlayer1")
    private int attemptsPlayer1;

    @Column(name="attemptsPlayer2")
    private int attemptsPlayer2;

    @Column(name="winner")
    private int winner;

    public Game() {}

    public Game(int player1, int player2, Place place1player1, Place place2player1, Place place1player2, Place place2player2, int attemptsPlayer1, int attemptsPlayer2, int winner) {
        this.player1 = player1;
        this.player2 = player2;
        this.place1player1 = place1player1;
        this.place2player1 = place2player1;
        this.place1player2 = place1player2;
        this.place2player2 = place2player2;
        this.attemptsPlayer1 = attemptsPlayer1;
        this.attemptsPlayer2 = attemptsPlayer2;
        this.winner = winner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayer1() {
        return player1;
    }

    public void setPlayer1(int player1) {
        this.player1 = player1;
    }

    public int getPlayer2() {
        return player2;
    }

    public void setPlayer2(int player2) {
        this.player2 = player2;
    }

    public Place getPlace1player1() {
        return place1player1;
    }

    public void setPlace1player1(Place place1player1) {
        this.place1player1 = place1player1;
    }

    public Place getPlace2player1() {
        return place2player1;
    }

    public void setPlace2player1(Place place2player1) {
        this.place2player1 = place2player1;
    }

    public Place getPlace1player2() {
        return place1player2;
    }

    public void setPlace1player2(Place place1player2) {
        this.place1player2 = place1player2;
    }

    public Place getPlace2player2() {
        return place2player2;
    }

    public void setPlace2player2(Place place2player2) {
        this.place2player2 = place2player2;
    }

    public int getAttemptsPlayer1() {
        return attemptsPlayer1;
    }

    public void setAttemptsPlayer1(int attemptsPlayer1) {
        this.attemptsPlayer1 = attemptsPlayer1;
    }

    public int getAttemptsPlayer2() {
        return attemptsPlayer2;
    }

    public void setAttemptsPlayer2(int attemptsPlayer2) {
        this.attemptsPlayer2 = attemptsPlayer2;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", place1player1=" + place1player1 +
                ", place2player1=" + place2player1 +
                ", place1player2=" + place1player2 +
                ", place2player2=" + place2player2 +
                ", attemptsPlayer1=" + attemptsPlayer1 +
                ", attemptsPlayer2=" + attemptsPlayer2 +
                ", winner=" + winner +
                '}';
    }
}
