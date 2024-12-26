package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class Knight extends Piece {
    public Knight(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        int xDiff = Math.abs(x - piece.x), yDiff = Math.abs(y - piece.y);
        if (xDiff == 1 && yDiff == 2 || xDiff == 2 && yDiff == 1) {
            if (piece.board.isEmpty(x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        int xDiff = Math.abs(x - piece.x), yDiff = Math.abs(y - piece.y);
        if (xDiff == 1 && yDiff == 2 || xDiff == 2 && yDiff == 1) {
            Tile tile = piece.board.getTile(x, y);
            if (piece.board.isPieceAt(x, y)) {
                if (tile.getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
                    return true;
                }
            }
        }
        return false;
    }
}