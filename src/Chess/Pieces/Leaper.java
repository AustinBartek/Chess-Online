package Chess.Pieces;

import Chess.Games.Variant;

public class Leaper extends Piece {

    public Leaper(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }
    
    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (ableToLeap(x, y, piece)) {
            return piece.board.getTile(x, y).isEmpty();
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        if (ableToLeap(x, y, piece)) {
            if (!piece.board.getTile(x, y).isEmpty()) {
                return piece.board.getTile(x, y).getPiece().canBeCaptured(piece.x, piece.y, x, y, piece);
            }
        }
        return false;
    }

    public boolean ableToLeap(int x, int y, Piece piece) {
        int count = getSurroundingPieces(piece);
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (x == piece.x) {
            if (Math.abs(y - piece.y) <= count) {
                return true;
            }
        } else if (y == piece.y) {
            if (Math.abs(x - piece.x) <= count) {
                return true;
            }
        }
        return false;
    }
    
    public int getSurroundingPieces(Piece piece) {
        int pieceCount = 0;
        for (int xPos = piece.x - 1; xPos <= piece.x + 1; xPos++) {
            for (int yPos = piece.y - 1; yPos <= piece.y + 1; yPos++) {
                if (piece.board.validPos(xPos, yPos)) {
                    if (!piece.board.getTile(xPos, yPos).isEmpty()) {
                        pieceCount++;
                    }
                }
            }
        }
        return pieceCount-1; //accounts for counting itself
    }
}