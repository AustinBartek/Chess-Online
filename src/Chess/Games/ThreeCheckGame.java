package Chess.Games;

import java.util.ArrayList;

import Chess.BoardStuff.Board;
import Chess.Pieces.*;
import Chess.Player.Player;

public class ThreeCheckGame extends Variant {
    int whiteChecks, blackChecks;
    ArrayList<int[]> checkList;

    public ThreeCheckGame (Board board, boolean online) {
        super(board, online);
        setupChecks();
        this.gameType = GameType.ThreeCheck;
    }

    private void setupChecks() {
        whiteChecks = blackChecks = 0;
        checkList = new ArrayList<>();
        checkList.add(new int[] {0, 0});
    }

    @Override
    public void addNewSave(int oldX, int oldY, int x, int y, Board oldBoard) {
        super.addNewSave(oldX, oldY, x, y, oldBoard);
        checkList.add(new int[] {whiteChecks, blackChecks});
    }

    @Override
    public void undo() {
        int[] newChecks = checkList.remove(checkList.size()-1);
        whiteChecks = newChecks[0];
        blackChecks = newChecks[1];
        super.undo();
    }

    @Override
    public void resetGame() {
        super.resetGame();
        setupChecks();
    }

    @Override
    public void tryMove(Piece piece, int x, int y, boolean rightClick) {
        int oldX = piece.x, oldY = piece.y;
        if (piece.canMove(x, y, piece) || piece.canCapture(x, y, piece)) {
            Board oldBoard = gameBoard.mockBoard();
            Player current = getCurrentPlayer();
            boolean currentWhite = current.isWhite();
            if (gameBoard.testMove(piece, x, y, currentWhite, true, rightClick, this)) {
                addNewSave(oldX, oldY, x, y, oldBoard);
                if (!gameBoard.testKingSafe(!current.isWhite())) {
                    if (current.isWhite()) {
                        whiteChecks++;
                    } else {
                        blackChecks++;
                    }
                }
                advanceGame(oldX, oldY, x, y);
            }
        }
    }

    @Override
    public EndType checkForWin(boolean white) {
        super.checkForWin(white);
        if (whiteChecks >= 3 && white) {
            return EndType.VARIANT;
        } else if (blackChecks >= 3 && !white) {
            return EndType.VARIANT;
        }
        return EndType.NONE;
    }
}