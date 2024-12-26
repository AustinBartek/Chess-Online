package Chess.Pieces;

import Chess.Games.Variant;

public class Amazon extends Piece {
    public Amazon(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        return new Queen(
                piece.game,
                piece.x,
                piece.y,
                piece.white).canMove(x, y, piece)
                || new Knight(
                        piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return new Queen(
                piece.game,
                piece.x,
                piece.y,
                piece.white).canCapture(x, y, piece)
                || new Knight(
                        piece.game,
                        piece.x,
                        piece.y,
                        piece.white).canCapture(x, y,
                                piece);
    }
}