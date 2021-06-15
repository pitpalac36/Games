package anagrame.domain;


import java.util.Arrays;

// for rest services
public class Dto {
    private String[] players;
    private String[] words;
    private String winner;
    private Integer[] points;

    public Dto(String[] players, String[] words, String winner, Integer[] points) {
        this.players = players;
        this.words = words;
        this.winner = winner;
        this.points = points;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Integer[] getPoints() {
        return points;
    }

    public void setPoints(Integer[] points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Dto{" +
                "players=" + Arrays.toString(players) +
                ", words=" + Arrays.toString(words) +
                ", winner='" + winner + '\'' +
                ", points=" + Arrays.toString(points) +
                '}';
    }
}
