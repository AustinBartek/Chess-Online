package Chess.BoardStuff;
import java.io.Serializable;

import Chess.Pieces.Piece;

public class Tile implements Serializable {
    protected Piece piece;

    public Tile() {
        this.piece = null;
    }

    public Tile(Piece piece) {
        this.piece = piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public boolean isEmpty() {
        return this.piece == null;
    }
}
