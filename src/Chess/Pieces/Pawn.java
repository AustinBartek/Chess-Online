package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class Pawn extends Piece {

    public int numMoves, lastMoveNumber;
    public boolean bigLeap, canPromote;

    public Pawn(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.numMoves = 0;
        this.bigLeap = false;
        this.canPromote = true;
    }

    @Override
    public Pawn copy(Board newBoard, boolean keepID) {
        Pawn returnPiece = (Pawn) super.copy(newBoard, keepID);
        returnPiece.numMoves = this.numMoves;
        returnPiece.lastMoveNumber = this.lastMoveNumber;
        returnPiece.bigLeap = this.bigLeap;
        returnPiece.canPromote = this.canPromote;
        return returnPiece;
    }
    
    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (!piece.board.isEmpty(x, y)) {
            return false;
        }
        if (x != piece.x) {
            return false;
        }

        int sign = (piece.white) ? -1 : +1;
        
        if (y == piece.y + 1 * sign) {
            return true;
        } else if (y == piece.y + 2 * sign) {
            if (piece.board.isEmpty(x, y - sign) && piece.initMove) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        int sign = (piece.white) ? -1 : +1;

        if (Math.abs(x - piece.x) != 1 || y != piece.y + 1 * sign) {
            return false;
        }
        
        if (piece.board.isPieceAt(x, y)) {
            if (piece.board.getTile(x, y).getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
                return true;
            }
        } else {
            if (piece.board.getTile(x, y - sign).getPiece() instanceof Pawn pawn) {
                if (pawn.numMoves == 1 && pawn.bigLeap && pawn.lastMoveNumber == piece.board.turnNumber - 1
                        && pawn.canBeCaptured(piece.x, piece.y, x, y - sign, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void promoteCheck() {
        if (this.canPromote) {
            if (isOnEdge(this.y)) {
                this.board.removePiece(this);
                this.board.allPieces.remove(this);
                this.board.addToBoard(new Queen(this.game, this.x, this.y, this.white));
            }
        }
    }

    public boolean isOnEdge(int y) {
        if (this.white) {
            return this.y == 0;
        } else {
            return this.y == this.board.height - 1;
        }
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        int sign = (piece.white) ? -1 : +1;
        if (oldX != x) {
            if (captured == null) {
                piece.board.removePiece(x, y - sign);
            }
        }
        if (Math.abs(oldY - y) == 2) {
            this.bigLeap = true;
        }
        this.numMoves++;
        this.lastMoveNumber = this.board.turnNumber;
        promoteCheck();
    }
}