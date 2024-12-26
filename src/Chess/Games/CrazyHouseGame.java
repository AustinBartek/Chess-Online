package Chess.Games;

import Chess.BoardStuff.Board;
import Chess.Pieces.*;

public class CrazyHouseGame extends Variant {
    public CrazyHouseGame(Board board, boolean online) {
        super(board, online);
        this.gameType = GameType.CrazyHouse;
        this.deadPanel.setAllowSelect(true);
    }

    @Override
    public void selectTile(int x, int y, boolean rightClick) {
        Piece putPiece = getPanelPiece();
        if (gameBoard.validPos(x, y)) {
            Piece pieceAtSpot = gameBoard.getTile(x, y).getPiece();
            if (putPiece != null) {
                if (pieceAtSpot == null) {
                    tryPiecePlacement(putPiece, x, y);
                    return;
                }
            }
            if (selectedPiece == null) {
                if (pieceAtSpot == null) {
                    return;
                }
                deadPanel.unselectPiece();
                Piece piece = pieceAtSpot;
                if (piece.white != currentPlayer.isWhite()) {
                    return;
                }
                selectedPiece = piece;
            } else {
                Piece pieceToUse = selectedPiece;
                selectedPiece = null;
                if (allowMove) {
                    tryMove(pieceToUse, x, y, rightClick);
                }
            }
        }
        gamePanel.repaint();
    }

    public Piece getPanelPiece() {
        return this.deadPanel.getSelectedPiece();
    }

    public void tryPiecePlacement(Piece putPiece, int x, int y) {
        int oldX = x, oldY = y;
        Board oldBoard = gameBoard.mockBoard(), testBoard = gameBoard.mockBoard();
        boolean color = currentPlayer.isWhite();
        Piece pieceToPlace = putPiece.copy(testBoard, false);
        pieceToPlace.x = x;
        pieceToPlace.y = y;
        pieceToPlace.white = color;
        pieceToPlace.init();
        testBoard.addToBoard(pieceToPlace);

        pieceToPlace.initMove = false;
        for (Piece test : testBoard.pieces) {
                test.onTurnEnd(pieceToPlace, oldX, oldY, x, y, color);
        }
        testBoard.turnNumber++;

        if (testBoard.overallSafety(color, this)) {
            this.gameBoard = testBoard;
            Piece removePiece = testBoard.getPieceByID(putPiece.getID(), true);
            testBoard.allPieces.remove(removePiece);
            testBoard.fixDeadPieces();
            addNewSave(oldX, oldY, x, y, oldBoard);
            advanceGame(oldX, oldY, x, y);
        }
    }

    /*
    public void tryMove(Piece piece, int x, int y, boolean rightClick) {
        int oldX = piece.x, oldY = piece.y;
        if (piece.canMove(x, y, piece) || piece.canCapture(x, y, piece)) {
            Board oldBoard = gameBoard.mockBoard();
            boolean currentWhite = currentPlayer.isWhite();
            if (gameBoard.testMove(piece, x, y, currentWhite, true, rightClick, this)) {
                addNewSave(oldX, oldY, x, y, oldBoard);
                advanceGame(oldX, oldY, x, y);
            }
        }
    }
    */
}