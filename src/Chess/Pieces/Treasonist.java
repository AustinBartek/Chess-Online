package Chess.Pieces;

import Chess.Games.Variant;

public class Treasonist extends Piece {

    public Treasonist(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return new Rook(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        Piece capturePiece = piece.board.getTile(x, y).getPiece();
        if (capturePiece == null) {
            return false;
        }
        boolean couldMove = false;
        piece.board.getTile(x, y).setPiece(null);
        if (new Rook(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece) && !(capturePiece instanceof King && capturePiece.white == piece.white)) {
            couldMove = true;
        }
        piece.board.getTile(x, y).setPiece(capturePiece);
        return couldMove;
    }

    @Override
    public boolean canBeCaptured(int oldX, int oldY, int x, int y, Piece attacker) {
        return true;
    }
}