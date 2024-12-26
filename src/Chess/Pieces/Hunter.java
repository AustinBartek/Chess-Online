package Chess.Pieces;

import java.util.ArrayList;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class Hunter extends Piece {

    public ArrayList<Piece> copyPieces;

    public Hunter(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.copyPieces = new ArrayList<>();
    }
    
    @Override
    public Hunter copy(Board newBoard, boolean keepID) {
        Hunter returnPiece = (Hunter) super.copy(newBoard, keepID);
        returnPiece.copyPieces = new ArrayList<>();
        for (Piece piece : this.copyPieces) {
            returnPiece.copyPieces.add(piece.copy(newBoard, false));
        }
        returnPiece.seesDead = this.seesDead;
        return returnPiece;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        boolean compound = false;
        for (Piece testPiece : this.copyPieces) {
            compound |= testPiece.canMove(x, y, piece);
        }
        return compound || new Knight(piece.game, x, y, piece.white).canMove(x, y, piece);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        boolean compound = false;
        for (Piece testPiece : this.copyPieces) {
            compound |= testPiece.canCapture(x, y, piece);
        }
        return compound || new Knight(piece.game, x, y, piece.white).canCapture(x, y, piece);
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        for (Piece testPiece : this.copyPieces) {
            testPiece.specialMoveEvent(oldX, oldY, x, y, captured, rightClick, piece);
        }
        if (captured != null) {
            if (captured.canBeCaptured(oldX, oldY, x, y, piece)) {
                Piece newCopy = captured.copy(piece.board, false);
                newCopy.white = piece.white;
                this.copyPieces.add(newCopy);
            }
        }
        updateProperties();
    }

    @Override
    public void onTurnEnd(Piece movedPiece, int oldX, int oldY, int x, int y, boolean white) {
        for (Piece testPiece : this.copyPieces) {
            testPiece.onTurnEnd(movedPiece, oldX, oldY, x, y, white);
        }
    }

    @Override
    public boolean invalidateTile(int x, int y, Piece attacker) { //for the purpose of making some pieces disallow the movement of pieces to be close to them
        boolean compound = false;
        for (Piece testPiece : this.copyPieces) {
            compound |= testPiece.invalidateTile(x, y, attacker);
        }
        return compound;
    }

    @Override
    public boolean blockTile(int x, int y, Piece attacker) {
        boolean compound = false;
        for (Piece testPiece : this.copyPieces) {
            compound |= testPiece.blockTile(x, y, attacker);
        }
        return compound;
    }

    @Override
    public boolean canBeCaptured(int oldX, int oldY, int x, int y, Piece attacker) {
        boolean compound = opposingPieces(attacker);
        for (Piece testPiece : this.copyPieces) {
            compound &= testPiece.canBeCaptured(oldX, oldY, x, y, attacker);
        }
        return compound;
    }

    public void updateProperties() {
        boolean compound = false;
        for (Piece piece : this.copyPieces) {
            compound |= piece.seesDead;
        }
        this.seesDead = compound;
    }
}