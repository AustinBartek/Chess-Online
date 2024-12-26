package Chess.Pieces;

import Chess.Games.Variant;

public class KingPawn extends Piece {

    public int numMoves, lastMoveNumber;
    public boolean bigLeap;

    public KingPawn(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.kingLike = true;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return new Pawn(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return new Pawn(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece);

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
    }
}