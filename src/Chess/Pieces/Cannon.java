package Chess.Pieces;

import Chess.Games.Variant;

public class Cannon extends Piece {

    public Cannon(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return new King(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return new Queen(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece);
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        Piece testPiece = piece.copy(piece.board, true);
        testPiece.x = oldX;
        testPiece.y = oldY;
        piece.board.getTile(x, y).setPiece(null);
        boolean couldMove = testPiece.canMove(x, y, testPiece);
        piece.board.getTile(x, y).setPiece(piece);
        if (!couldMove && captured != null) {
            if (captured.white != piece.white) {
                piece.board.movePiece(piece, oldX, oldY);
            }
        }
    }
}