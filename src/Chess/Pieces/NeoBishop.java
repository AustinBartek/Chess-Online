package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class NeoBishop extends Piece {

    public NeoBishop(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (Math.abs(x - piece.x) - Math.abs(y - piece.y) == 0) {
            int stepX = (int) Math.signum(x - piece.x), stepY = (int) Math.signum(y - piece.y);
            int testX = piece.x + stepX, testY = piece.y + stepY;
            boolean clear = true, skip = true;
            while (testX != x) {
                boolean good = piece.board.canSlideThrough(testX, testY, piece);
                clear &= good || skip;
                if (!good) {
                    skip = false;
                }
                testX += stepX;
                testY += stepY;
            }
            clear &= piece.board.isEmpty(x, y);
            if (clear) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (Math.abs(x - piece.x) - Math.abs(y - piece.y) == 0) {
            int stepX = (int) Math.signum(x - piece.x), stepY = (int) Math.signum(y - piece.y);
            int testX = piece.x + stepX, testY = piece.y + stepY;
            boolean clear = true, skip = true;
            while (testX != x) {
                boolean good = piece.board.canSlideThrough(testX, testY, piece);
                clear &= good || skip;
                if (!good) {
                    skip = false;
                }
                testX += stepX;
                testY += stepY;
            }
            if (clear) {
                Tile tile = piece.board.getTile(x, y);
                if (piece.board.isPieceAt(x, y)) {
                    if (tile.getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
