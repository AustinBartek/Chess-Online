package Chess.Pieces;

import Chess.Games.Variant;

public class Switcher extends Piece {

    public Switcher(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (piece.board.isPieceAt(x, y) && !(x == piece.x && y == piece.y)) {
            if (piece.board.getTile(x, y).getPiece().white == piece.white) {
                if (!piece.board.getTile(x, y).getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return false;
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        if (captured != null) {
            if (!captured.canBeCaptured(oldX, oldY, x, y, piece)) {
                captured.x = oldX;
                captured.y = oldY;
                piece.board.addToBoard(captured);
            }
        }
    }
}