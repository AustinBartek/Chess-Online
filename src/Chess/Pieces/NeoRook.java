package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class NeoRook extends Piece {

    public NeoRook(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (x == piece.x) {
            int step = (int) Math.signum(y - piece.y);
            int testY = piece.y + step;
            boolean clear = true, skip = true;
            while (testY != y && clear) {
                boolean good = piece.board.canSlideThrough(x, testY, piece);
                clear &= good || skip;
                if (!good) {
                    skip = false;
                }
                testY += step;
            }
            clear &= piece.board.isEmpty(x, y);
            if (clear) {
                return true;
            }
        } else if (y == piece.y) {
            int step = (int) Math.signum(x - piece.x);
            int testX = piece.x + step;
            boolean clear = true, skip = true;
            while (testX != x && clear) {
                boolean good = piece.board.canSlideThrough(testX, y, piece);
                clear &= good || skip;
                if (!good) {
                    skip = false;
                }
                testX += step;
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
        Tile tile = piece.board.getTile(x, y);
        if (!piece.board.isPieceAt(x, y)) {
            return false;
        }
        if (!tile.getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
            return false;
        }
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (x == piece.x) {
            int step = (int) Math.signum(y - piece.y);
            int testY = piece.y + step;
            boolean clear = true, skip = true;
            while (testY != y && clear) {
                boolean good = piece.board.canSlideThrough(x, testY, piece);
                clear &= good || skip;
                if (!good) {
                    skip = false;
                }
                testY += step;
            }
            if (clear) {
                return true;
            }
        } else if (y == piece.y) {
            int step = (int) Math.signum(x - piece.x);
            int testX = piece.x + step;
            boolean clear = true, skip = true;
            while (testX != x && clear) {
                boolean good = piece.board.canSlideThrough(testX, y, piece);
                clear &= good || skip;
                if (!good) {
                    skip = false;
                }
                testX += step;
            }
            if (clear) {
                return true;
            }
        }
        return false;
    }
}