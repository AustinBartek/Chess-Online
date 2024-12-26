package Chess.Pieces;

import Chess.Games.Variant;

public class KnightRider extends Piece {

    public KnightRider(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        int[] xJumps = { -2, -1, 1, 2 }, yJumps = { -2, -1, 1, 2 };
        for (int xJump : xJumps) {
            for (int yJump : yJumps) {
                if (Math.abs(xJump) == Math.abs(yJump)) {
                    continue;
                }
                int xPos = piece.x + xJump, yPos = piece.y + yJump;
                while (piece.board.validPos(xPos, yPos)) {
                    if (piece.board.isPieceAt(xPos, yPos)) {
                        break;
                    }
                    if (x == xPos && y == yPos) {
                        return true;
                    }
                    xPos += xJump;
                    yPos += yJump;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        if (!piece.board.isPieceAt(x, y)) {
            return false;
        }
        if (!piece.board.getTile(x, y).getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
            return false;
        }
        int[] xJumps = { -2, -1, 1, 2 }, yJumps = { -2, -1, 1, 2 };
        for (int xJump : xJumps) {
            for (int yJump : yJumps) {
                if (Math.abs(xJump) == Math.abs(yJump)) {
                    continue;
                }
                int xPos = piece.x + xJump, yPos = piece.y + yJump;
                while (piece.board.validPos(xPos, yPos)) {
                    if (x == xPos && y == yPos) {
                        return true;
                    }
                    if (piece.board.isPieceAt(xPos, yPos)) {
                        break;
                    }
                    xPos += xJump;
                    yPos += yJump;
                }
            }
        }
        return false;
    }
}