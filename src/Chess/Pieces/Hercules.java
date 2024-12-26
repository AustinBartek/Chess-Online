package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class Hercules extends Piece {

    public int index;

    public Hercules(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }
    
    @Override
    public Hercules copy(Board newBoard, boolean keepID) {
        Hercules returnPiece = (Hercules) super.copy(newBoard, keepID);
        returnPiece.index = this.index;
        return returnPiece;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return false;
    }
    
    @Override
    public void onTurnEnd(Piece piece, int oldX, int oldY, int x, int y, boolean white) {
        if (white == this.white) {
            if (this.index <= 10) {
                this.index++;
            }
        }
    }
}