package Chess.BoardStuff;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import Chess.Games.Variant;
import Chess.Pieces.*;

public class Board implements Serializable {
        public int width, height, turnNumber;
        protected Tile[][] tiles;
        public ArrayList<Piece> pieces, deadPieces, allPieces;
        public Color col1, col2;

        public Board() {
                this.width = 8;
                this.height = 8;
                initTiles();
                initProperties();
        }

        public Board(int width, int height) {
                this.width = Math.max(width, 1);
                this.height = Math.max(height, 1);
                initTiles();
                initProperties();
        }

        protected final void initTiles() {
                this.tiles = new Tile[this.width][this.height];
                for (Tile[] col : this.tiles) {
                        for (int i = 0; i < col.length; i++) {
                                col[i] = new Tile();
                        }
                }
        }

        public Board mockBoard() {
                Board returnBoard = new Board(this.width, this.height);
                for (Piece piece : this.pieces) {
                        Piece newPiece = piece.copy(returnBoard, true);
                        returnBoard.addToBoard(newPiece);
                }
                for (Piece deadPiece : this.deadPieces) {
                        Piece newPiece = deadPiece.copy(returnBoard, true);
                        returnBoard.deadPieces.add(newPiece);
                        returnBoard.allPieces.add(newPiece);
                }
                returnBoard.turnNumber = this.turnNumber;
                returnBoard.col1 = this.col1;
                returnBoard.col2 = this.col2;
                return returnBoard;
        }

        private void initProperties() {
                this.pieces = new ArrayList<>();
                this.deadPieces = new ArrayList<>();
                this.allPieces = new ArrayList<>();
                this.col1 = new Color(150, 180, 150);
                this.col2 = new Color(100, 150, 100);
                this.turnNumber = 0;
        }

        public void addToBoard(Piece piece) {
                if (validPos(piece.x, piece.y)) {
                        if (this.getTile(piece.x, piece.y).getPiece() != null) {
                                removePiece(this.getTile(piece.x, piece.y).getPiece());
                        }
                        this.getTile(piece.x, piece.y).setPiece(piece);
                        this.pieces.add(piece);
                        if (!this.allPieces.contains(piece)) {
                                this.allPieces.add(piece);
                        }
                }
        }

        public void addToBoard(Piece piece, boolean countForAll) {
                if (validPos(piece.x, piece.y)) {
                        if (this.getTile(piece.x, piece.y).getPiece() != null) {
                                removePiece(this.getTile(piece.x, piece.y).getPiece());
                        }
                        this.getTile(piece.x, piece.y).setPiece(piece);
                        this.pieces.add(piece);
                        if (!this.allPieces.contains(piece) && countForAll) {
                                this.allPieces.add(piece);
                        }
                }
        }

        public Tile getTile(int x, int y) {
                if (validPos(x, y)) {
                        return this.tiles[x][y];
                } else {
                        System.out.println("invalid tile position reached");
                        return null;
                }
        }

        public boolean validPos(int x, int y) {
                return (x >= 0 && x < this.width && y >= 0 && y < this.height);
        }

        public final void setUpClassicGame() {
                BoardSetup classic = BoardSetup.generateClassicBoard(this);
                classic.setup();
        }

        public final void setUp960Game() {
                BoardSetup chess960 = BoardSetup.generate960Board(this);
                chess960.setup();
        }

        public final void setUpHordeGame() {
                BoardSetup horde = BoardSetup.generateHordeSetup(this);
                horde.setup();
        }
        
        public boolean testBoth(Piece piece, int x, int y, boolean white, Variant game) {
                return testMove(piece, x, y, white, false, true, game)
                                || testMove(piece, x, y, white, false, false, game);
        }

        public boolean testMove(Piece piece, int x, int y, boolean white, boolean keep, boolean rightClick,
                        Variant game) { //returns true if the board was changed
                boolean returnVal = false;
                switch (game.getGameType()) {
                        case AntiChess -> returnVal = testAntiChessMove(piece, x, y, white, keep, rightClick, game);
                        case Atomic -> returnVal = testAtomicMove(piece, x, y, white, keep, rightClick, game);
                        case Classic, KingOfTheHill, ThreeCheck, DoubleMove, CrazyHouse ->
                                returnVal = testClassicMove(piece, x, y, white, keep, rightClick, game);
                        default -> {
                        }
                }
                return returnVal;
        }
        
        public boolean performBaseMove(Piece piece, int x, int y, boolean white, boolean rightClick) {
                if (!preMoveConditions(x, y, piece)) {
                        return false;
                }

                int oldX = piece.x, oldY = piece.y;
                Piece usePiece = this.getTile(oldX, oldY).getPiece();
                Piece captured = this.movePiece(usePiece, x, y);
                usePiece.specialMoveEvent(oldX, oldY, x, y, captured, rightClick, usePiece);
                usePiece.initMove = false;
                for (Piece test : this.pieces) {
                        test.onTurnEnd(usePiece, oldX, oldY, x, y, white);
                }
                this.turnNumber++;

                return true;
        }

        public boolean testClassicMove(Piece piece, int x, int y, boolean white, boolean keep, boolean rightClick, Variant game) {
                if (!preMoveConditions(x, y, piece)) {
                        return false;
                }

                Board testBoard = this.mockBoard();
                testBoard.performBaseMove(piece, x, y, white, rightClick);

                boolean safe = testBoard.overallSafety(white, game);

                if (keep && safe) { // reverts to the original board before the move happened here
                        adoptBoard(testBoard);
                }
                fixDeadPieces();
                return safe;
        }

        public boolean testAtomicMove(Piece piece, int x, int y, boolean white, boolean keep, boolean rightClick, Variant game) {
                if (!preMoveConditions(x, y, piece)) {
                        return false;
                }

                int oldX = piece.x, oldY = piece.y;
                Board testBoard = mockBoard();
                Piece usePiece = testBoard.getTile(oldX, oldY).getPiece();
                Piece captured = testBoard.movePiece(usePiece, x, y);
                usePiece.specialMoveEvent(oldX, oldY, x, y, captured, rightClick, usePiece);

                if (captured instanceof Piece) { //the atomic explosion happens here!
                                for (int xPos = x - 1; xPos <= x + 1; xPos++) {
                                        for (int yPos = y - 1; yPos <= y + 1; yPos++) {
                                                if (!validPos(xPos, yPos)) {
                                                        continue;
                                                }
                                                if (!(testBoard.getTile(xPos, yPos).getPiece() instanceof Pawn)) {
                                                        testBoard.removePiece(xPos, yPos);
                                                }
                                        }
                                }
                        testBoard.removePiece(x, y);
                }

                for (Piece test : testBoard.pieces) {
                        test.onTurnEnd(usePiece, oldX, oldY, x, y, white);
                }
                testBoard.turnNumber++;

                boolean safe = testBoard.overallSafety(white, game);
                for (int testX = 0; testX < testBoard.width; testX++) {
                        for (int testY = 0; testY < testBoard.height; testY++) {
                                if (!(testBoard.getTile(testX, testY).getPiece() instanceof Piece)) {
                                        continue;
                                }
                                Piece piece1 = testBoard.getTile(testX, testY).getPiece();
                                for (int xPos = testX - 1; xPos <= testX + 1; xPos++) {
                                        for (int yPos = testY - 1; yPos <= testY + 1; yPos++) {
                                                if (!validPos(xPos, yPos) || xPos == testX && yPos == testY) {
                                                        continue;
                                                }
                                                if (!(testBoard.getTile(xPos, yPos).getPiece() instanceof Piece)) {
                                                        continue;
                                                }
                                                Piece piece2 = testBoard.getTile(xPos, yPos).getPiece();
                                                if (piece1.white != piece2.white
                                                                && (piece1.kingLike && piece2.kingLike)) {
                                                        safe = true;
                                                }
                                        }
                                }
                        }
                }
                if (testBoard.countKings(!white) == 0 && testBoard.countKings(white) > 0) {
                        safe = true;
                }

                if (keep && safe) { // reverts to the original board before the move happened here
                        usePiece.initMove = false;
                        adoptBoard(testBoard);
                }
                fixDeadPieces();
                return safe;
        }

        public boolean testAntiChessMove(Piece piece, int x, int y, boolean white, boolean keep, boolean rightClick,
                        Variant game) {
                if (!preMoveConditions(x, y, piece)) {
                        return false;
                }

                boolean allowed = (piece.canCapture(x, y, piece) || !canPlayerCapture(white)); //this line ensures that you have to capture if you can in antichess.
                if (!keep || !allowed) {
                        return allowed;
                }

                Board testBoard = mockBoard();
                testBoard.performBaseMove(piece, x, y, white, rightClick);

                if (keep && allowed) {
                        adoptBoard(testBoard);
                }
                fixDeadPieces();
                return true;
        }
        
        public boolean preMoveConditions(int x, int y, Piece piece) {
                if (!piece.preMoveCheck(x, y)) {
                        return false;
                }
                if (isTileInvalid(x, y, piece) || isBlocked(x, y, piece)) {
                        return false;
                }
                return true;
        }

        public void adoptBoard(Board board) {
                this.tiles = board.tiles;
                this.pieces = board.pieces;
                this.deadPieces = board.deadPieces;
                this.allPieces = board.allPieces;
                this.turnNumber = board.turnNumber;
        }

        public void fixDeadPieces() {
                this.deadPieces = new ArrayList<>();
                for (Piece testPiece : this.allPieces) {
                        if (!this.pieces.contains(testPiece)) {
                                this.deadPieces.add(testPiece);
                        }
                }
        }

        public boolean testKingSafe(boolean white) {
                int safeKings = 0, kings = 0;
                for (Piece currentPiece : this.pieces) {
                        if (currentPiece.kingLike && currentPiece.white == white) {
                                kings++;
                                if (!currentPiece.inDanger()) {
                                        safeKings++;
                                }
                        }
                }
                return (safeKings > 0 || kings > 1);
        }

        public int countKings(boolean white) {
                int kingCount = 0;
                for (Piece currentPiece : this.pieces) {
                        if (currentPiece.kingLike && currentPiece.white == white) {
                                kingCount++;
                        }
                }
                return kingCount;
        }

        public int remainingPieces(boolean white) {
                int count = 0;
                for (int x = 0; x < this.width; x++) {
                        for (int y = 0; y < this.height; y++) {
                                if (getTile(x, y).getPiece() != null) {
                                        if (getTile(x, y).getPiece().white == white) {
                                                count++;
                                        }
                                }
                        }
                }
                return count;
        }

        public boolean overallSafety(boolean white, Variant game) {
                return testKingSafe(white) || (game.hordeRulesApply(white) && remainingPieces(white) > 0);
        }

        public boolean isSquareSafe(boolean white, int x, int y) {
                Pawn testPiece = new Pawn(null, x, y, white);
                testPiece.setBoard(this);
                Piece savePiece = this.getTile(x, y).getPiece();
                this.getTile(x, y).setPiece(testPiece);
                boolean returnVal = !(testPiece.inDanger());
                this.getTile(x, y).setPiece(savePiece);
                return returnVal;
        }

        public Piece movePiece(Piece piece, int x, int y) {
                if (!validPos(x, y)) {
                        return null;
                }
                Piece returnPiece = null;
                removePiece(piece);
                piece.x = x;
                piece.y = y;
                if (getTile(x, y).getPiece() != null) {
                        Piece captured = getTile(x, y).getPiece();
                        removePiece(captured);
                        returnPiece = captured;
                }
                addToBoard(piece, piece.countsForAll);
                return returnPiece;
        }

        public boolean canPlayerMove(boolean white, boolean legalMove, Variant game) {
                for (Piece piece : this.pieces) {
                        if (piece.white != white) {
                                continue;
                        }
                        for (int x = 0; x < this.width; x++) {
                                for (int y = 0; y < this.height; y++) {
                                        if (piece.canMove(x, y, piece) || piece.canCapture(x, y, piece)) {
                                                if (testBoth(piece, x, y, piece.white, game) || !legalMove) {
                                                        return true;
                                                }
                                        }
                                }
                        }
                }
                return false;
        }

        public boolean canPlayerCapture(boolean white) { //for antichess, checking whether it's possible for a player to capture with any pieces.
                for (Piece piece : this.pieces) {
                        if (piece.white != white) {
                                continue;
                        }
                        for (int x = 0; x < this.width; x++) {
                                for (int y = 0; y < this.height; y++) {
                                        if (piece.canCapture(x, y, piece)) {
                                                return true;
                                        }
                                }
                        }
                }
                return false;
        }

        public Piece removePiece(Piece piece) {
                if (piece == null) {
                        return null;
                }
                this.getTile(piece.x, piece.y).setPiece(null);
                this.pieces.remove(piece);
                return piece;
        }

        public Piece removePiece(int x, int y) {
                if (!validPos(x, y)) {
                        return null;
                }
                if (getTile(x, y).getPiece() != null) {
                        Piece piece = getTile(x, y).getPiece();
                        getTile(x, y).setPiece(null);
                        this.pieces.remove(piece);
                        return piece;
                } else {
                        return null;
                }
        }

        public Piece removePiece(long id) {
                Piece pieceToRemove = getPieceByID(id);
                return removePiece(pieceToRemove);
        }

        public boolean isBlocked(int x, int y, Piece attacker) {
                for (Piece piece : this.pieces) {
                        if (piece == attacker) {
                                continue;
                        }
                        if (piece.blockTile(x, y, attacker)) {
                                return true;
                        }
                }
                return false;
        }

        public boolean isTileInvalid(int x, int y, Piece attacker) {
                for (Piece currentPiece : this.pieces) {
                        if (currentPiece == attacker) {
                                continue;
                        }
                        if (currentPiece.invalidateTile(x, y, attacker)) {
                                return true;
                        }
                }
                return false;
        }

        public boolean isPieceAt(int x, int y) {
                return getTile(x, y).getPiece() != null;
        }

        public boolean isEmpty(int x, int y) {
                return !isPieceAt(x, y);
        }

        public boolean canSlideThrough(int x, int y, Piece attacker) {
                return isEmpty(x, y) && !isBlocked(x, y, attacker);
        }

        public Piece getRecentDead(int x, int y) {
                Piece returnPiece = null;
                for (Piece dead : this.deadPieces) {
                        if (dead.x == x && dead.y == y) {
                                returnPiece = dead;
                        }
                }
                return returnPiece;
        }

        public Piece getRecentDead(int x, int y, boolean white) {
                Piece returnPiece = null;
                for (Piece dead : this.deadPieces) {
                        if (dead.x == x && dead.y == y && dead.white == white) {
                                returnPiece = dead;
                        }
                }
                return returnPiece;
        }

        public Piece getPieceByID(long ID) {
                for (Piece piece : this.pieces) {
                        if (piece.getID() == ID) {
                                return piece;
                        }
                }
                return null;
        }

        public Piece getPieceByID(long ID, boolean checkDead) {
                ArrayList<Piece> useList;
                if (checkDead) {
                        useList = this.allPieces;
                } else {
                        useList = this.pieces;
                }
                for (Piece piece : useList) {
                        if (piece.getID() == ID) {
                                return piece;
                        }
                }
                return null;
        }

        public boolean hasKing(boolean white) {
                for (int x = 0; x < this.width; x++) {
                        for (int y = 0; y < this.height; y++) {
                                if (this.getTile(x, y).getPiece() == null) {
                                        continue;
                                }
                                Piece piece = this.getTile(x, y).getPiece();
                                if (piece.kingLike && piece.white == white) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        public void reset() {
                this.turnNumber = 0;
                emptyPieceLists();
                initTiles();
                setUpClassicGame();
        }

        public void emptyPieceLists() {
                this.pieces = new ArrayList<>();
                this.allPieces = new ArrayList<>();
                this.deadPieces = new ArrayList<>();
        }
}