package Chess.Games.UI;

import javax.swing.*;

import Chess.Pieces.*;

public class PieceButton extends JButton {
    private Piece piece;

    public PieceButton(Piece piece) {
        this.piece = piece;
        if (piece instanceof Piece) {
            this.setToolTipText(piece.description);
        }
    }

    public Piece getPiece() {
        return this.piece;
    }
}