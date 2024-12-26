package Chess.Pieces;

import Chess.Games.Variant;

public class Bomber extends Piece {

    public Bomber(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return new Rook(
                piece.game,
                piece.x,
                piece.y,
                piece.white).canMove(x, y, piece)
                || new Bishop(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return new Rook(
                piece.game,
                piece.x,
                piece.y,
                piece.white).canCapture(x, y, piece)
                || new Bishop(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece);
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        if (captured != null) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (piece.board.validPos(i, j)) {
                        piece.board.removePiece(i, j);
                    }
                }
            }
        }
    }
}