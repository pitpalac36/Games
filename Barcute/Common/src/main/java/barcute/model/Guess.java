package barcute.model;

public class Guess {
    private boolean place1;
    private boolean place2;

    public Guess() {
        place1 = false;
        place2 = false;
    }

    public boolean getPlace1() {
        return place1;
    }

    public void setPlace1(boolean place1) {
        this.place1 = place1;
    }

    public boolean getPlace2() {
        return place2;
    }

    public void setPlace2(boolean place2) {
        this.place2 = place2;
    }


}
