package Chess.Pieces;

import Chess.Games.Variant;

public class Queen extends Piece {

    public Queen(Variant game, int x, int y, boolean color) {
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
}