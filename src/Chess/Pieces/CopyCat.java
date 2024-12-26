package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.*;

public class CopyCat extends Piece {

    public Piece copyPiece;

    public CopyCat(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.copyPiece = null;
    }
    
    @Override
    public CopyCat copy(Board newBoard, boolean keepID) {
        CopyCat returnPiece = (CopyCat) super.copy(newBoard, keepID);
        if (this.copyPiece != null) {
            returnPiece.copyPiece = this.copyPiece.copy(newBoard, false);
        }
        return returnPiece;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (this.copyPiece != null) {
            return this.copyPiece.canMove(x, y, piece);
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        if (this.copyPiece != null) {
            return this.copyPiece.canCapture(x, y, piece);
        }
        return false;
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        if (this.copyPiece != null) {
            this.copyPiece.specialMoveEvent(oldX, oldY, x, y, captured, rightClick, piece);
        }
    }

    @Override
    public void onTurnEnd(Piece movedPiece, int oldX, int oldY, int x, int y, boolean white) {
        if (white != this.white) {
            if (movedPiece instanceof CopyCat) {
                return;
            }
            Piece newPiece = getPieceOnPreviousTurn(movedPiece);
            if (newPiece == null) {
                return;
            }
            newPiece.white = this.white;
            this.seesDead = newPiece.seesDead;
            this.copyPiece = newPiece;
        } else {
            if (this.copyPiece != null) {
                this.copyPiece.onTurnEnd(movedPiece, oldX, oldY, x, y, white);
            }
        }
    }

    public Piece getPieceOnPreviousTurn(Piece piece) {
        GameStateSave[] saves = this.game.getCurrentSaves();
        if (saves.length > 0) {
            Piece test = saves[saves.length - 1].boardSave.getPieceByID(piece.getID());
            if (test == null) {
                return null;
            }
            return test.copy(this.board, false);
        }
        return piece.copy(this.board, false);
    }

    @Override
    public boolean invalidateTile(int x, int y, Piece attacker) { //for the purpose of making some pieces disallow the movement of pieces to be close to them
        if (this.copyPiece != null) {
            return this.copyPiece.invalidateTile(x, y, attacker);
        }
        return false;
    }

    @Override
    public boolean blockTile(int x, int y, Piece attacker) {
        if (this.copyPiece != null) {
            return this.copyPiece.blockTile(x, y, attacker);
        }
        return false;
    }
}