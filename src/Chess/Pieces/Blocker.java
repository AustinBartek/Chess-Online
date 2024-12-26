package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class Blocker extends Piece {

    public boolean active;
    public int activeTime;

    public Blocker(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.active = false;
        this.activeTime = 0;
    }

    @Override
    public Blocker copy(Board newBoard, boolean keepID) {
        Blocker returnPiece = (Blocker) super.copy(newBoard, keepID);
        returnPiece.active = this.active;
        returnPiece.activeTime = this.activeTime;
        return returnPiece;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return new Rook(
                piece.game,
                piece.x,
                piece.y,
                piece.white).canMove(x, y, piece)
                || new Knight(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece) || (x == this.x && y == this.y && !this.active);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return new Rook(
                piece.game,
                piece.x,
                piece.y,
                piece.white).canCapture(x, y,
                        piece)
                || new Knight(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece);
    }

    @Override
    public boolean blockTile(int x, int y, Piece attacker) {
        if (this.active) {
            if (attacker.white == this.white) {
                return false;
            }
            if (Math.abs(x - this.x) <= 1 && Math.abs(y - this.y) <= 1 && !(this.x == x && this.y == y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        this.active = oldX == x && oldY == y;
    }

    @Override
    public void onTurnEnd(Piece piece, int oldX, int oldY, int x, int y, boolean white) {
        if (white == this.white && activeTime > 0) {
            this.active = false;
            this.activeTime = 0;
        } else if (white == this.white && this.active) {
            this.activeTime++;
        }
    }
}