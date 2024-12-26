package Chess.Pieces;

import Chess.Games.Variant;

public class Achilles extends Piece {
    public Achilles(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return moveTest(x, y, piece) && piece.board.getTile(x, y).isEmpty();
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        if (piece.board.getTile(x, y).getPiece() == null) {
            return false;
        }
        return moveTest(x, y, piece) && piece.board.getTile(x, y).getPiece().canBeCaptured(piece.x, piece.y, x, y, piece);
    }

    @Override
    public boolean canBeCaptured(int oldX, int oldY, int x, int y, Piece attacker) {
        if (!opposingPieces(attacker)) {
            return false;
        }
        int xDiff = Math.abs(oldX - x), yDiff = Math.abs(oldY - y);
        return xDiff > 1 || yDiff > 1;
    }

    public boolean moveTest(int x, int y, Piece piece) {
        int xDiff = Math.abs(piece.x - x), yDiff = Math.abs(piece.y - y);
        return (xDiff <= 1 && yDiff <= 1 && (xDiff + yDiff) > 0);
    }
}