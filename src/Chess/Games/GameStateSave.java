package Chess.Games;

import java.io.Serializable;

import Chess.BoardStuff.Board;

public class GameStateSave implements Serializable {
    public Board boardSave;
    public int oldX, oldY, newX, newY;
    public boolean whiteTurn;
    public double whiteTimeLeft, blackTimeLeft;

    public GameStateSave(Board save, int oX, int oY, int nX, int nY, boolean turn, double wTL, double bTL) {
        this.boardSave = save;
        this.oldX = oX;
        this.oldY = oY;
        this.newX = nX;
        this.newY = nY;
        this.whiteTurn = turn;
        this.whiteTimeLeft = wTL;
        this.blackTimeLeft = bTL;
    }
}