package Chess.Pieces;

import java.awt.*;

import Chess.Games.Variant;

public class Priest extends Piece {

    public Priest(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.seesDead = true;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        Piece dead = piece.board.getRecentDead(x, y, piece.white);
        boolean deadAlly = false;
        if (dead != null) {
            deadAlly = !(dead instanceof Priest) && !piece.board.isPieceAt(x, y);
        }
        return new Switcher(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece) || deadAlly
                || new Knight(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece);
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        return false;
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        //feel free to figure out how to make rightclicking change if the move simply moves the piece (if it can) or if the move revives a piece.
        //there you go. Oh thanks
        Piece dead = piece.board.getRecentDead(x, y, piece.white);
        if (dead != null) {
            piece.board.deadPieces.remove(dead);
            piece.board.getTile(x, y).setPiece(null);
            Piece testPiece = piece.copy(piece.board, true);
            testPiece.x = oldX;
            testPiece.y = oldY;
            boolean couldMove = testPiece.canMove(x, y, testPiece);
            piece.board.deadPieces.add(dead);
            piece.board.getTile(x, y).setPiece(piece);
            if (couldMove && !rightClick && captured == null) {
                return;
            }
        }
        if (captured != null) {
            if (captured.white == piece.white) {
                new Switcher(piece.game, piece.x, piece.y, piece.white).specialMoveEvent(oldX, oldY, x, y,
                        captured,
                        rightClick, piece);
            }
        } else {
            Piece revived = piece.board.getRecentDead(x, y, piece.white);
            if (revived != null) {
                piece.board.movePiece(piece, oldX, oldY);
                piece.board.removePiece(piece);
                piece.board.addToBoard(revived);
            }
        }
    }

    @Override
    public void specialGraphics(Graphics g, int tileSize, int offX, int offY, boolean selected) {
        if (selected) {
            for (int x = 0; x < this.board.width; x++) {
                for (int y = 0; y < this.board.height; y++) {
                    if (this.canMove(x, y, this) && this.board.getRecentDead(x, y, this.white) != null) {
                        g.setColor(Color.yellow);
                        g.drawOval(x * tileSize + offX, y * tileSize + offY, tileSize, tileSize);
                    }
                }
            }
        }
    }
}