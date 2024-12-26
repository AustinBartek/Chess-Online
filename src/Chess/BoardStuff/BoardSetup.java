package Chess.BoardStuff;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

import Chess.Pieces.*;

public final class BoardSetup implements Serializable {
    private final int width, height;
    private final Tile[][] tiles;
    private final Board board;

    public BoardSetup(Board board, int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[this.width][this.height];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                this.tiles[x][y] = new Tile();
            }
        }
        this.board = board;
    }

    public static BoardSetup generate960Board(Board board) {
        Random rand = new Random();
        BoardSetup setup = new BoardSetup(board, 8, 8);
        setup.fillRow(6, new Pawn(null, 0, 0, true));
        ArrayList<Integer> openXPoses = new ArrayList<>(), openForBishops = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            openXPoses.add(i);
        }
        Rook rook1 = new Rook(null, 0, 0, true), rook2 = new Rook(null, 0, 0, true);
        Bishop bish1 = new Bishop(null, 0, 0, true), bish2 = new Bishop(null, 0, 0, true);
        King king = new King(null, 0, 0, true);
        Knight knight1 = new Knight(null, 0, 0, true), knight2 = new Knight(null, 0, 0, true);
        Queen queen = new Queen(null, 0, 0, true);
        int rook1X = rand.nextInt(6), rook2X;
        if (rand.nextBoolean() || rook1X < 2) {
            rook2X = 7 - ((rook1X != 5) ? rand.nextInt(5 - rook1X) : 0);
        } else {
            rook2X = rand.nextInt(rook1X-1);
        }
        int kingX = rand.nextInt(Math.abs(rook2X - rook1X) - 1) + 1 + Math.min(rook1X, rook2X);
        openXPoses.remove((Integer)rook1X);
        openXPoses.remove((Integer)rook2X);
        openXPoses.remove((Integer)kingX);
        int bish1X = openXPoses.remove(rand.nextInt(openXPoses.size()));
        openForBishops.addAll(openXPoses);
        Predicate<Integer> pred = new Predicate<Integer>() {
            @Override
            public boolean test(Integer t) {
                return t % 2 == bish1X % 2;
            }
        };
        openForBishops.removeIf(pred);
        int bish2X = openForBishops.remove(rand.nextInt(openForBishops.size()));
        openXPoses.remove((Integer)bish2X);
        int knight1X = openXPoses.remove(rand.nextInt(openXPoses.size())),
                knight2X = openXPoses.remove(rand.nextInt(openXPoses.size())),
                queenX = openXPoses.remove(rand.nextInt(openXPoses.size()));
        setup.putPiece(rook1X, 7, rook1);
        setup.putPiece(rook2X, 7, rook2);
        setup.putPiece(bish1X, 7, bish1);
        setup.putPiece(bish2X, 7, bish2);
        setup.putPiece(knight1X, 7, knight1);
        setup.putPiece(knight2X, 7, knight2);
        setup.putPiece(queenX, 7, queen);
        setup.putPiece(kingX, 7, king);
        setup.mirrorBoard(true, false, false);
        return setup;
    }

    public static BoardSetup generateClassicBoard(Board board) {
        BoardSetup setup = new BoardSetup(board, 8, 8);
        setup.fillRow(6, new Pawn(null, 0, 0, true));
        ArrayList<Piece> remainingPieces = new ArrayList<>();
        remainingPieces.add(new Rook(null, 0, 0, true));
        remainingPieces.add(new Knight(null, 0, 0, true));
        remainingPieces.add(new Bishop(null, 0, 0, true));
        remainingPieces.add(new Queen(null, 0, 0, true));
        remainingPieces.add(new King(null, 0, 0, true));
        remainingPieces.add(new Bishop(null, 0, 0, true));
        remainingPieces.add(new Knight(null, 0, 0, true));
        remainingPieces.add(new Rook(null, 0, 0, true));
        int x = 0;
        while (!remainingPieces.isEmpty()) {
            Piece current = remainingPieces.remove(0);
            setup.putPiece(x, 7, current);
            x++;
        }
        setup.mirrorBoard(true, false, false);
        return setup;
    }

    public static BoardSetup generateHordeSetup(Board board) {
        BoardSetup setup = new BoardSetup(board, 8, 8);
        Pawn nonDouble = new Pawn(null, 0, 0, true);
        nonDouble.initMove = false;
        setup.fillArea(0, 6, 8, 2, new Pawn(null, 0, 0, true));
        setup.fillArea(0, 4, 8, 2, nonDouble);
        setup.fillArea(1, 3, 2, 1, nonDouble);
        setup.fillArea(5, 3, 2, 1, nonDouble);
        ArrayList<Piece> remainingPieces = new ArrayList<>();
        remainingPieces.add(new Rook(null, 0, 0, false));
        remainingPieces.add(new Knight(null, 0, 0, false));
        remainingPieces.add(new Bishop(null, 0, 0, false));
        for (int n = 0; n < 3; n++) {
            Piece current = remainingPieces.remove(0);
            for (int i = 0; i < 2; i++) {
                if (i % 2 == 1) {
                    setup.putPiece(7 - n, 0, current);
                } else {
                    setup.putPiece(n, 0, current);
                }
            }
        }
        setup.putPiece(3, 0, new Queen(null, 0, 0, false));
        setup.putPiece(4, 0, new King(null, 0, 0, false));
        setup.fillRow(1, new Pawn(null, 0, 0, false));
        return setup;
    }

    public void setup() {
        this.board.width = this.width;
        this.board.height = this.height;
        this.board.emptyPieceLists();
        this.board.initTiles();
        this.board.turnNumber = 0;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (!this.tiles[x][y].isEmpty()) {
                    this.board.addToBoard(this.tiles[x][y].getPiece());
                }
            }
        }
    }

    public void fillRow(int y, Piece piece) {
        if (y >= 0 && y < this.height) {
            for (int x = 0; x < this.width; x++) {
                fitPiece(x, y, piece);
            }
        }
    }

    public void fillColumn(int x, Piece piece) {
        if (x >= 0 && x < this.width) {
            for (int y = 0; y < this.height; y++) {
                fitPiece(x, y, piece);
            }
        }
    }

    public void putPiece(int x, int y, Piece piece) {
        if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
            fitPiece(x, y, piece);
        }
    }

    public void fillArea(int x, int y, int sizeX, int sizeY, Piece piece) {
        for (int xPos = x; xPos < x + sizeX; xPos++) {
            for (int yPos = y; yPos < y + sizeY; yPos++) {
                if (xPos >= 0 && xPos < this.width && yPos >= 0 && yPos < this.height) {
                    fitPiece(xPos, yPos, piece);
                }
            }
        }
    }

    public void mirrorBoard(boolean yAxis, boolean upperOrLeft, boolean sameColor) {
        int minX = this.width/2, minY = this.height/2, maxX = this.width, maxY = this.height;
        if (upperOrLeft) {
            minX = minY = 0;
            maxX = this.width / 2;
            maxY = this.height / 2;
        }
        if (!yAxis) {
            for (int x = minX; x < maxX / 2; x++) {
                for (int y = 0; y < height; y++) {
                    if (this.tiles[x][y].getPiece() == null) {
                        continue;
                    }
                    Piece newPiece = this.tiles[x][y].getPiece().copy(this.board, false);
                    if (!sameColor) {
                        newPiece.white = !newPiece.white;
                    }
                    fitPiece(this.width - x - 1, y, newPiece);
                }
            }
        } else {
            for (int x = 0; x < width; x++) {
                for (int y = minY; y < maxY; y++) {
                    if (this.tiles[x][y].getPiece() == null) {
                        continue;
                    }
                    Piece newPiece = this.tiles[x][y].getPiece().copy(this.board, false);
                    if (!sameColor) {
                        newPiece.white = !newPiece.white;
                    }
                    fitPiece(x, this.height - y - 1, newPiece);
                }
            }
        }
    }

    public void fitPiece(int x, int y, Piece piece) {
        Piece returnPiece = null;
        if (piece != null) {
            returnPiece = piece.copy(this.board, false);
            returnPiece.x = x;
            returnPiece.y = y;
        }
        this.tiles[x][y].setPiece(returnPiece);
    }
}
