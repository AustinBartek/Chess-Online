package Chess.Pieces;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class King extends Piece {
    public boolean allowCastle = true;

    public King(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.kingLike = true;
    }

    @Override
    public Piece copy(Board newBoard, boolean keepID) {
        King returnPiece = (King) super.copy(newBoard, keepID);
        returnPiece.allowCastle = this.allowCastle;
        return returnPiece;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (piece.board.getTile(x, y).getPiece() != null) {
            return false;
        }
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (Math.abs(x - piece.x) <= 1 && Math.abs(y - piece.y) <= 1) {
            return true;
        }
        if (piece.inDanger()) {
            return false;
        }
        if (x - piece.x == -2 && y == piece.y) {
            boolean clear = true;
            int leftDistance = 0;
            if (piece.initMove) {
                for (int xPos = piece.x - 1; xPos > 0; xPos--) {
                    if (piece.board.validPos(xPos, piece.y)) {
                        clear &= piece.board.isEmpty(xPos,
                                piece.y) && piece.board.isSquareSafe(piece.white, xPos, piece.y);
                    }
                    leftDistance++;
                }
                if (clear && leftDistance >= 2) {
                    if (piece.board.getTile(0, piece.y).getPiece() instanceof Rook rook) {
                        if (rook.initMove && allowCastle) {
                            return true;
                        }
                    }
                }
            }
        } else if (x - piece.x == 2 && y == piece.y) {
            boolean clear = true;
            int rightDistance = 0;
            for (int xPos = piece.x + 1; xPos < piece.board.width - 1; xPos++) {
                if (piece.board.validPos(xPos, piece.y)) {
                    clear &= piece.board.isEmpty(xPos,
                            piece.y) && piece.board.isSquareSafe(piece.white, xPos, piece.y);
                }
                rightDistance++;
            }
            if (clear && rightDistance >= 2) {
                if (piece.board.getTile(piece.board.width - 1, piece.y).getPiece() instanceof Rook rook) {
                    if (rook.initMove && allowCastle) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        if (x == piece.x && y == piece.y) {
            return false;
        }
        if (Math.abs(x - piece.x) <= 1 && Math.abs(y - piece.y) <= 1) {
            Tile tile = piece.board.getTile(x, y);
            if (piece.board.isPieceAt(x, y)) {
                if (tile.getPiece().canBeCaptured(piece.x, piece.y, x, y, piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        if (piece instanceof King) {
            if (Math.abs(x - oldX) > 1) {
                if (x > oldX) {
                    Piece usePiece = piece.board.getTile(piece.board.width - 1, y).getPiece();
                    if (usePiece != null) {
                        piece.board.movePiece(usePiece, x - 1, y);
                    }
                } else {
                    Piece usePiece = piece.board.getTile(0, y).getPiece();
                    if (usePiece != null) {
                        piece.board.movePiece(usePiece, x + 1, y);
                    }
                }
            }
        }
    }
}