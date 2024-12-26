package Chess.Pieces;

import java.util.Random;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public class Schizophrenic extends Piece {

    public int index;
    public Random rand;

    public Schizophrenic(Variant game, int x, int y, boolean color) {
        super(game, x, y, color);
        this.rand = new Random();
        this.index = this.rand.nextInt(4);
    }
    
    @Override
    public Piece copy(Board newBoard, boolean keepID) {
        Schizophrenic returnPiece = (Schizophrenic) super.copy(newBoard, keepID);
        returnPiece.index = this.index;
        returnPiece.rand = this.rand;
        return returnPiece;
    }

    @Override
    public boolean canMove(int x, int y, Piece piece) {
        boolean[] moves = {new Pawn(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece),
            new Knight(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece),
            new Bishop(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece),
            new Rook(piece.game, piece.x, piece.y, piece.white).canMove(x, y, piece)
        };
        return moves[this.index];
    }

    @Override
    public boolean canCapture(int x, int y, Piece piece) {
        boolean[] moves = { new Pawn(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece),
                new Knight(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece),
                new Bishop(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece),
                new Rook(piece.game, piece.x, piece.y, piece.white).canCapture(x, y, piece)
        };
        return moves[this.index];
    }
    
    @Override
    public void onTurnEnd(Piece piece, int oldX, int oldY, int x, int y, boolean white) {
        if (white == this.white) {
            this.index = this.rand.nextInt(4);
        }
    }
}