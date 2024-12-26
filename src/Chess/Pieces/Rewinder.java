package Chess.Pieces;

import Chess.BoardStuff.Board;
import Chess.Games.*;

public class Rewinder extends Piece {
    private boolean rewind;

    public Rewinder(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.rewind = false;
    }

    @Override
    public Rewinder copy(Board newBoard, boolean keepID) {
        Rewinder newPiece = (Rewinder) super.copy(newBoard, keepID);
        newPiece.rewind = this.rewind;
        return newPiece;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        if (this.rewind) {
            Piece test = piece.board.getTile(x, y).getPiece();
            if (test == null) {
                return false;
            }
            if (!test.opposingPieces(piece)) {
                return false;
            }
            int[] oldPos = getOldPosition(test);
            if (oldPos[0] == -1) {
                return false;
            }
            if (!piece.board.getTile(oldPos[0], oldPos[1]).isEmpty()) {
                return false;
            }
            return (this.board.testBoth(test, oldPos[0], oldPos[1], piece.white, this.game));
        } else {
            return new Rook(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece);
        }
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        if (!this.rewind) {
            return new Rook(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece);
        }
        return false;
    }

    @Override
    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) {
        if (this.rewind && captured != null) {
            int[] oldPos = getOldPosition(captured);
            captured.x = oldPos[0];
            captured.y = oldPos[1];
            piece.board.addToBoard(captured);
            piece.board.removePiece(piece);
            piece.x = oldX;
            piece.y = oldY;
            piece.board.addToBoard(piece);
        }
        this.rewind = !this.rewind;
    }

    public int[] getOldPosition(Piece piece) {
        if (piece == null) {
            return new int[] { -1, -1 };
        }
        GameStateSave[] saves = this.game.getCurrentSaves();
        GameStateSave save1, save2, save3;
        switch (saves.length) {
            case 0:
            return new int[] {-1, -1};
            case 1: case 2:
            save1 = save2 = save3 = saves[saves.length-1];
            break;
            case 3: case 4:
            save1 = saves[saves.length-1];
            save2 = save3 = saves[saves.length-3];
            break;
            default:
            save1 = saves[saves.length-1];
            save2 = saves[saves.length-3];
            save3 = saves[saves.length-5];
        }
        Piece prev1, prev2, prev3;
        prev1 = save1.boardSave.getPieceByID(piece.getID());
        prev2 = save2.boardSave.getPieceByID(piece.getID());
        prev3 = save3.boardSave.getPieceByID(piece.getID());
        if (piece.x != prev1.x || piece.y != prev1.y) {
            return new int[] {prev1.x, prev1.y};
        } else if (prev1.x != prev2.x || prev1.y != prev2.y) {
            return new int[] {prev2.x, prev2.y};
        } else if (prev2.x != prev3.x || prev2.y != prev3.y) {
            return new int[] {prev3.x, prev3.y};
        }
        return new int[] {-1, -1};
    }
}