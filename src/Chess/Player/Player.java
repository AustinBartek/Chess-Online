package Chess.Player;

import java.io.Serializable;

public class Player implements Serializable {
    private final boolean white;
    private double time;
    
    public Player(boolean white, double time) {
        this.white = white;
        this.time = time;
    }

    public void changeTime(double num) {
        this.time += num;
    }

    public void setTime(double num) {
        this.time = num;
    }

    public double getTime() {
        return this.time;
    }

    public boolean isWhite() {
        return this.white;
    }

    public boolean outOfTime() {
        return this.time <= 0;
    }
}