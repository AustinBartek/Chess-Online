package Chess.Games.UI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.function.Predicate;
import javax.swing.*;

import Chess.Games.Variant;
import Chess.Pieces.Piece;

public class DeadPiecePanel extends JPanel {
    private final Variant game;

    private ArrayList<Piece> whitePieceList, blackPieceList;
    private HashMap<Piece, Integer> whiteCounts, blackCounts;
    private boolean allowSelection;
    private Piece selectedPiece;

    private final static Dimension initSize = new Dimension(200, 800);
    private final static Font numberFont = new Font(Font.MONOSPACED, Font.BOLD, 1);

    public DeadPiecePanel(Variant game) {
        this.game = game;
        this.allowSelection = false;
        this.selectedPiece = null;
        this.setPreferredSize(initSize);
        this.updatePieces();

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                trySelect(x, y);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    public void adjustInfo() {
        this.selectedPiece = null;
        updatePieces();
        updateDimensions();
        repaint();
    }

    public Piece getSelectedPiece() {
        return this.selectedPiece;
    }

    public void setAllowSelect(boolean allow) {
        this.allowSelection = allow;
    }

    @Override
    public void paintComponent(Graphics g) {
        ArrayList<Piece> whitePiecesToDraw = getOverallDrawingList(whiteCounts),
                blackPiecesToDraw = getOverallDrawingList(blackCounts);
        int numWhitePieces = whiteCounts.size(), numBlackPieces = blackCounts.size();
        int height = this.getHeight(), width = this.getWidth();
        int quarterWidth = width / 4, tenthHeight = height / 10;
        int whiteXOffset = width * 5 / 8, blackXOffset = width / 8;
        int whitePieceSize, blackPieceSize;

        if (quarterWidth * (numWhitePieces) + tenthHeight * 2 > height) { //adjusts pieceSize to the white pieces
            whitePieceSize = (height - tenthHeight * 2) / (numWhitePieces);
        } else {
            whitePieceSize = quarterWidth;
        }
        if (quarterWidth * (numBlackPieces) + tenthHeight * 2 > height) { //adjusts pieceSize to the black pieces
            blackPieceSize = (height - tenthHeight * 2) / (numBlackPieces);
        } else {
            blackPieceSize = quarterWidth;
        }

        g.setFont(numberFont);
        g.setColor(new Color(100, 50, 0));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(75, 25, 0));
        g.fillRect(whiteXOffset, tenthHeight, quarterWidth, height - tenthHeight * 2);
        g.fillRect(blackXOffset, tenthHeight, quarterWidth, height - tenthHeight * 2);

        g.setColor(Color.red);
        int positionIndex = 0;
        for (Piece piece : whitePiecesToDraw) {
            int currentCount = whiteCounts.get(piece);
            Image currentImage = piece.image.getImage();
            String numString = "x" + currentCount;
            fitFontSize(g, numString, whitePieceSize / 2);
            int yOffset = positionIndex * whitePieceSize + tenthHeight;

            if (matchesSelectedPiece(piece)) {
                g.setColor(Color.yellow);
                g.fillRect(whiteXOffset, yOffset, quarterWidth, whitePieceSize);
                g.setColor(Color.red);
            }
            g.drawImage(currentImage, whiteXOffset, yOffset, whitePieceSize, whitePieceSize, null);
            if (currentCount > 1) {
                g.drawString(numString, whiteXOffset + whitePieceSize / 4, yOffset + whitePieceSize);
            }

            positionIndex++;
        }

        positionIndex = 0;
        for (Piece piece : blackPiecesToDraw) {
            int currentCount = blackCounts.get(piece);
            String numString = "x" + currentCount;
            fitFontSize(g, numString, blackPieceSize / 2);
            Image currentImage = piece.image.getImage();
            int yOffset = positionIndex * blackPieceSize + tenthHeight;

            if (matchesSelectedPiece(piece)) {
                g.setColor(Color.yellow);
                g.fillRect(blackXOffset, yOffset, quarterWidth, blackPieceSize);
                g.setColor(Color.red);
            }
            g.drawImage(currentImage, blackXOffset, yOffset, blackPieceSize, blackPieceSize, null);
            if (currentCount > 1) {
                g.drawString(numString, blackXOffset + blackPieceSize / 4, yOffset + blackPieceSize);
            }

            positionIndex++;
        }
    }

    private boolean matchesSelectedPiece(Piece piece) {
        if (this.selectedPiece == null) {
            return false;
        }
        return (piece.white == this.selectedPiece.white && piece.getClass().equals(selectedPiece.getClass()));
    }
    
    private void fitFontSize(Graphics g, String text, int width) {
        Font newFont = numberFont;
        FontMetrics metrics = g.getFontMetrics(newFont);
        float currentSize = 1;

        while (metrics.stringWidth(text) < width) {
            currentSize++;
            newFont = newFont.deriveFont(currentSize);
            metrics = g.getFontMetrics(newFont);
        }

        currentSize--;
        newFont = newFont.deriveFont(currentSize);
        g.setFont(newFont);
    }

    private int getPieceIndex(Piece piece) {
        String name = piece.getBasicName();
        String[] list = Piece.nameList;
        for (int i = 0; i < list.length; i++) {
            String currentName = list[i];
            if (name.equals(currentName)) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Piece> convertSetToList(Set<Piece> set) {
        Iterator<Piece> iter = set.iterator();
        ArrayList<Piece> newList = new ArrayList<>();
        while (iter.hasNext()) {
            newList.add(iter.next());
        }
        return newList;
    }

    private ArrayList<Piece> getOverallDrawingList(HashMap<Piece, Integer> counts) {
        ArrayList<Piece> returnList = convertSetToList(counts.keySet());
        sortByPieceIndex(returnList);
        return returnList;
    }

    private void updatePieces() {
        ArrayList<Piece> dead = this.game.gameBoard.deadPieces;
        this.whitePieceList = new ArrayList<>(dead);
        filterColor(this.whitePieceList, true);
        this.blackPieceList = new ArrayList<>(dead);
        filterColor(this.blackPieceList, false);
        this.whiteCounts = countPieces(this.whitePieceList);
        this.blackCounts = countPieces(this.blackPieceList);
    }

    private void updateDimensions() {
        int numPieces = Math.max(this.countPieces(this.whitePieceList).size(), this.countPieces(this.blackPieceList).size());
        int currentWidth = this.getWidth();
        int newHeight = (int) (currentWidth/200.0 * (numPieces+2) * 50);
        this.setPreferredSize(new Dimension(200, newHeight));
    }

    private void filterColor(ArrayList<Piece> pieces, boolean white) {
        Predicate<Piece> pred = (Piece t) -> t.white != white;
        pieces.removeIf(pred);
    }

    private void sortByPieceIndex(java.util.List<Piece> pieces) {
        pieces.sort((Piece o1, Piece o2) -> getPieceIndex(o1) - getPieceIndex(o2));
    }

    private HashMap<Piece, Integer> countPieces(ArrayList<Piece> pieces) {
        HashMap<Piece, Integer> counts = new HashMap<>();
        for (Piece piece : pieces) {

            boolean found = false;
            for (Piece contained : counts.keySet()) {
                if (contained.getClass().equals(piece.getClass())) {
                    found = true;
                    counts.put(contained, counts.get(contained) + 1);
                    break;
                }
            }

            if (!found) {
                counts.put(piece, 1);
            }
        }
        return counts;
    }

    private void trySelect(int x, int y) {
        game.unselectPiece();

        if (!allowSelection) {
            return;
        }

        Piece selected = null;

        updatePieces();
        updateDimensions();
        ArrayList<Piece> whitePiecesToDraw = getOverallDrawingList(whiteCounts),
                blackPiecesToDraw = getOverallDrawingList(blackCounts);
        int numWhitePieces = whiteCounts.size(), numBlackPieces = blackCounts.size();
        int height = this.getHeight(), width = this.getWidth();
        int quarterWidth = width / 4, tenthHeight = height / 10;
        int whiteXOffset = width * 5 / 8, blackXOffset = width / 8;
        int whitePieceSize, blackPieceSize;

        if (quarterWidth * (numWhitePieces) + tenthHeight * 2 > height) { //adjusts pieceSize to the white pieces
            whitePieceSize = (height - tenthHeight * 2) / (numWhitePieces);
        } else {
            whitePieceSize = quarterWidth;
        }
        if (quarterWidth * (numBlackPieces) + tenthHeight * 2 > height) { //adjusts pieceSize to the black pieces
            blackPieceSize = (height - tenthHeight * 2) / (numBlackPieces);
        } else {
            blackPieceSize = quarterWidth;
        }

        boolean whiteTurn = game.getCurrentPlayer().isWhite();
        boolean whiteSelect = (x >= whiteXOffset && x <= whiteXOffset + quarterWidth),
                blackSelect = (x >= blackXOffset && x <= blackXOffset + quarterWidth);

        if (whiteSelect && !whiteTurn) {
            int slotNumber = (y - tenthHeight) / whitePieceSize;
            if (slotNumber < whitePiecesToDraw.size()) {
                selected = whitePiecesToDraw.get(slotNumber);
            }
        } else if (blackSelect && whiteTurn) {
            int slotNumber = (y - tenthHeight) / blackPieceSize;
            if (slotNumber < blackPiecesToDraw.size()) {
                selected = blackPiecesToDraw.get(slotNumber);
            }
        }

        this.selectedPiece = selected;
        repaint();
    }
    
    public void unselectPiece() {
        this.selectedPiece = null;
        repaint();
    }
}