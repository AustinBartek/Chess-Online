package Chess.Games;

import java.awt.*;

import Chess.BoardStuff.Board;
import Chess.Pieces.Piece;

public class DoubleMoveGame extends Variant {
    private int turnCount;

    public DoubleMoveGame(Board board, boolean online) {
        super(board, online);
        this.gameType = GameType.DoubleMove;
        this.turnCount = 1;
    }

    @Override
    public void tryMove(Piece piece, int x, int y, boolean rightClick) {
        Board oldBoard = gameBoard.mockBoard();
        boolean currentWhite = getCurrentPlayer().isWhite();
        int oldX = piece.x, oldY = piece.y;

        if (this.turnCount == 1) {
            if (piece.canMove(x, y, piece) || piece.canCapture(x, y, piece)) {
                if (gameBoard.testMove(piece, x, y, currentWhite, true, rightClick, this)) {
                    addNewSave(oldX, oldY, x, y, oldBoard);
                    advanceGame(oldX, oldY, x, y);
                    this.turnCount = 0;
                }
            }
        } else {
            if (piece.canMove(x, y, piece) || piece.canCapture(x, y, piece)) {
                if (gameBoard.performBaseMove(piece, x, y, currentWhite, rightClick)) {
                    addNewSave(oldX, oldY, x, y, oldBoard);
                    updatePastPositions(oldX, oldY, x, y);
                    updateWin();
                    this.turnCount++;
                }
            }
        }
    }

    @Override
    public void setTileColor(int x, int y, Piece selectedPiece, Graphics g) {
        if (selectedPiece.canCapture(x, y, selectedPiece)
                || selectedPiece.canMove(x, y, selectedPiece)) {
            if (gameBoard.preMoveConditions(x, y, selectedPiece) && turnCount == 0 || gameBoard.testBoth(selectedPiece, x, y, selectedPiece.white, this)) {
                if (selectedPiece.canCapture(x, y, selectedPiece)) {
                    g.setColor(averageColors(g.getColor(), Color.red));
                } else if (selectedPiece.canMove(x, y, selectedPiece)) {
                    g.setColor(averageColors(g.getColor(), Color.yellow));
                }
            } else if (gameBoard.isBlocked(x, y, selectedPiece)) {
                g.setColor(averageColors(g.getColor(), Color.green));
            } else if (gameBoard.isTileInvalid(x, y, selectedPiece)) {
                g.setColor(averageColors(g.getColor(), Color.blue));
            } else {
                g.setColor(averageColors(g.getColor(), Color.darkGray));
            }
        }
    }

    @Override
    public void resetGame() {
        super.resetGame();
        this.turnCount = 1;
    }

    @Override
    public void undo() {
        super.undo();
        this.turnCount--;
        if (this.turnCount < 0) {
            this.turnCount = 1;
        }
    }
}