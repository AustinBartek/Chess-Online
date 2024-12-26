package Chess.Games;

import Chess.BoardStuff.Board;
import Chess.Pieces.*;
import Chess.Player.Player;

public class AntiChessGame extends Variant {
    public AntiChessGame (Board board, boolean online) {
        super(board, online);
        for (Piece piece: board.allPieces) {
            if (piece instanceof King king) {
                king.allowCastle = false;
            }
        }
        this.gameType = GameType.AntiChess;
    }

    @Override
    public EndType checkForWin(boolean white) {
        if (gameBoard.remainingPieces(white) == 0) {
            return EndType.VARIANT;
        }
        if (!gameBoard.canPlayerMove(white, false, this)) {
            return EndType.STALEMATEWIN;
        }

        Player testPlayer;
        if (white) {
            testPlayer = getWhitePlayer();
        } else {
            testPlayer = getBlackPlayer();
        }
        if (testPlayer.outOfTime()) {
            return EndType.TIME;
        }

        return EndType.NONE;
    }
}