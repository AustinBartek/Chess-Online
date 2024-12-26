package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class Rook extends Piece {

    public Rook(Variant game, int x, int y, boolean color) {
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
            boolean clear = true;
            while (testY != y) {
                clear &= piece.board.canSlideThrough(x, testY, piece);
                testY += step;
            }
            clear &= piece.board.isEmpty(x, y);
            if (clear) {
                return true;
            }
        } else if (y == piece.y) {
            int step = (int) Math.signum(x - piece.x);
            int testX = piece.x + step;
            boolean clear = true;
            while (testX != x) {
                clear &= piece.board.canSlideThrough(testX, y, piece);
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
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (x == piece.x) {
            int step = (int) Math.signum(y - piece.y);
            int testY = piece.y + step;
            boolean clear = true;
            while (testY != y) {
                clear &= piece.board.canSlideThrough(x, testY, piece);
                testY += step;
            }
            if (clear) {
                if (piece.board.isPieceAt(x, y)) {
                    if (piece.board.getTile(x, y).getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
                        return true;
                    }
                }
            }
        } else if (y == piece.y) {
            int step = (int) Math.signum(x - piece.x);
            int testX = piece.x + step;
            boolean clear = true;
            while (testX != x) {
                clear &= piece.board.canSlideThrough(testX, y, piece);
                testX += step;
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