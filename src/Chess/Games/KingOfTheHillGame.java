package Chess.Games;

import java.awt.*;

import Chess.BoardStuff.Board;
import Chess.Pieces.*;

public class KingOfTheHillGame extends Variant {
    private final int minX, minY, maxX, maxY;
    
    public KingOfTheHillGame(Board board, boolean online) {
        super(board, online);
        this.minX = (board.width-1)/2;
        this.minY = (board.height-1)/2;
        this.maxX = board.width / 2;
        this.maxY = board.height / 2;
        this.gameType = GameType.KingOfTheHill;
    }

    @Override
    public EndType checkForWin(boolean white) {
        EndType base = super.checkForWin(white);
        if (base != EndType.NONE) {
            return base;
        }
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (gameBoard.getTile(x, y).getPiece() instanceof Piece) {
                    Piece piece = gameBoard.getTile(x, y).getPiece();
                    if (piece.white == white && piece.kingLike) {
                        return EndType.VARIANT;
                    }
                }
            }
        }
        return EndType.NONE;
    }

    @Override
    public void extraBoardGraphics(int tileSize, int offsetX, int offsetY, Graphics g) {
        g.setColor(Color.yellow);
        int width = tileSize/8, leftBound = minX * tileSize + offsetX, rightBound = (maxX + 1) * tileSize + offsetX, topBound = minY * tileSize + offsetY, bottomBound = (maxY + 1) * tileSize + offsetY;
        g.fillRect(leftBound, topBound, width, bottomBound-topBound);
        g.fillRect(leftBound, bottomBound-width, rightBound-leftBound, width);
        g.fillRect(leftBound, topBound, rightBound-leftBound, width);
        g.fillRect(rightBound-width, topBound, width, bottomBound-topBound);
    }
}