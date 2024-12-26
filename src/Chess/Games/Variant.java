package Chess.Games;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.*;

import Chess.Main;
import Chess.BoardStuff.Board;
import Chess.Pieces.Piece;
import Chess.Player.Player;
import Chess.UI.DeadPiecePanel;
import Chess.UI.TimerPanel;
import Online.Packets.MoveRequest;
import Online.Packets.UpdateGameRequest;

public abstract class Variant extends JPanel {
    protected final JPanel gamePanel, sidePanel;
    protected final DeadPiecePanel deadPanel;
    protected final TimerPanel timerPanel;
    protected final JButton resetButton, undoButton;
    protected final ArrayList<GameStateSave> previousBoards;
    protected final JCheckBox boardFlipToggle;
    protected final boolean whiteKingInit, blackKingInit, isOnline;
    protected final Player whitePlayer, blackPlayer;

    protected Player currentPlayer;
    protected int sizeX, sizeY, offsetX, offsetY, tileSize, pastX = -1, pastY = -1, newX = -1, newY = -1;
    protected boolean whiteWin = false, blackWin = false, allowMove = true, ready = true;
    protected transient boolean boardFlip;
    protected EndType winType;
    protected Piece selectedPiece;
    protected GameType gameType;

    public Board gameBoard;
    public final Board originalBoard;

    private Long ID = -1l;

    public Variant(Board board, boolean online) {
        Variant reference = this;

        this.isOnline = online;
        if (online) {
            this.ready = false;
            this.allowMove = false;
        }

        this.whitePlayer = new Player(true, 600);
        this.blackPlayer = new Player(false, 600);
        this.currentPlayer = whitePlayer;
        this.selectedPiece = null;

        setLayout(new BorderLayout());

        this.gameBoard = board;
        for (Piece piece : board.allPieces) {
            piece.setGame(reference);
        }
        this.originalBoard = this.gameBoard.mockBoard();
        this.winType = EndType.NONE;
        this.previousBoards = new ArrayList<>();
        this.previousBoards.add(new GameStateSave(originalBoard.mockBoard(), pastX, pastY, newX, newY,
                currentPlayer.isWhite(), whitePlayer.getTime(), blackPlayer.getTime()));
        this.whiteKingInit = board.hasKing(true);
        this.blackKingInit = board.hasKing(false);

        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                // getting all the numbers required to scale the screen properly :)
                Rectangle rect = gamePanel.getBounds();
                sizeX = (int) rect.getWidth();
                sizeY = (int) rect.getHeight();
                int smallestSize = Math.min(sizeX, sizeY);
                int biggestDimension = Math.max(gameBoard.width, gameBoard.height);
                tileSize = smallestSize / biggestDimension;
                offsetX = (sizeX - tileSize * gameBoard.width) / 2;
                offsetY = (sizeY - tileSize * gameBoard.height) / 2;

                //flips the drawing if the boardflip setting is true
                int flipOffsetX = 0, flipOffsetY = 0;
                if (boardFlip) {
                    flipOffsetX = sizeX - tileSize - offsetX * 2;
                    flipOffsetY = sizeY - tileSize - offsetY * 2;
                }

                int boardWidth = gameBoard.width * tileSize, boardHeight = gameBoard.height * tileSize;

                // draw the background
                g.setColor(Color.gray);
                g.fillRect(0, 0, sizeX, sizeY);

                boolean showMoves = selectedPiece != null;
                for (int x = 0; x < gameBoard.width; x++) {
                    for (int y = 0; y < gameBoard.height; y++) {
                        int xPos = Math.abs(flipOffsetX - x * tileSize) + offsetX,
                                yPos = Math.abs(flipOffsetY - y * tileSize) + offsetY;

                        // coloring of the tiles for indicators
                        if (x % 2 == y % 2) {
                            g.setColor(gameBoard.col1);
                        } else {
                            g.setColor(gameBoard.col2);
                        }
                        if (x == pastX && y == pastY || x == newX && y == newY) { // indicating the last move made
                            g.setColor(averageColors(g.getColor(), new Color(190, 120, 0)));
                        }
                        if (showMoves) {
                            setTileColor(x, y, selectedPiece, g);
                        }
                        if (gameBoard.getTile(x, y).getPiece() instanceof Piece) {
                            Piece piece = gameBoard.getTile(x, y).getPiece();
                            if (piece.kingLike) {
                                if (piece.white == currentPlayer.isWhite()
                                        && !gameBoard.testKingSafe(currentPlayer.isWhite())) {
                                    g.setColor(averageColors(g.getColor(), Color.red.darker()));
                                }
                            }
                        }
                        g.fillRect(xPos, yPos, tileSize, tileSize);

                        // any extra visuals that the games themselves would offer are drawn here
                        extraBoardGraphics(tileSize, offsetX, offsetY, g);

                        // drawing the pieces to the screen after all the tiles are done
                        if (gameBoard.isPieceAt(x, y)) {
                            Piece piece = gameBoard.getTile(x, y).getPiece();
                            g.drawImage(piece.image.getImage(), xPos, yPos, tileSize, tileSize, null);
                        }
                        for (Piece piece : gameBoard.pieces) {
                            piece.specialGraphics(g, tileSize, offsetX, offsetY, false);
                        }
                        if (showMoves) {
                            if (selectedPiece.seesDead) { // draws partially transparent dead pieces for pieces that
                                                          // interact with those
                                if (gameBoard.getRecentDead(x, y) != null && !gameBoard.isPieceAt(x, y)) {
                                    Piece dead = gameBoard.getRecentDead(x, y);
                                    g.drawImage(dead.deadImage.getImage(), xPos, yPos, tileSize, tileSize,
                                            null);
                                }
                            }
                            selectedPiece.specialGraphics(g, tileSize, offsetX, offsetY, true);
                        }
                    }
                }

                // victory messages :)
                g.setColor(Color.yellow);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, Math.max(boardWidth, boardHeight) / 8));
                String message = winType.text;
                String secondMessage = "";
                if (whiteWin) {
                    secondMessage = "White wins";
                } else if (blackWin) {
                    secondMessage = "Black wins";
                }
                if (!ready) {
                    message = "Waiting";
                    secondMessage = "For Opponent";
                }
                Rectangle2D stringRect = g.getFontMetrics().getStringBounds(message, g);
                int width = (int) stringRect.getWidth(), height = (int) stringRect.getHeight();
                Rectangle2D stringRect2 = g.getFontMetrics().getStringBounds(secondMessage, g);
                int width2 = (int) stringRect2.getWidth(), height2 = (int) stringRect2.getHeight();
                g.drawString(message, offsetX + (boardWidth - (boardWidth / 2 + width / 2)),
                        offsetY + (boardHeight - (boardHeight / 2 + height / 2)));
                g.drawString(secondMessage, offsetX + (boardWidth - (boardWidth / 2 + width2 / 2)),
                        offsetY + (boardHeight - (boardHeight / 2 - height2 / 2)));
            }
        };

        gamePanel.setPreferredSize(new Dimension(800, 800));

        this.deadPanel = new DeadPiecePanel(reference);

        this.timerPanel = new TimerPanel(this);

        this.boardFlipToggle = new JCheckBox("Board Flip");
        this.boardFlipToggle.setFocusable(false);
        this.boardFlipToggle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        this.boardFlipToggle.setBackground(Color.gray.brighter());
        this.boardFlipToggle.setForeground(Color.black);
        this.boardFlipToggle.setPreferredSize(new Dimension(150, 50));

        this.undoButton = new JButton("Undo Last Move");
        this.undoButton.setBackground(Color.orange.darker());
        this.undoButton.setFocusable(false);
        this.undoButton.setPreferredSize(new Dimension(150, 50));
        this.undoButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        this.undoButton.setForeground(Color.white);

        this.resetButton = new JButton("Reset Game");
        this.resetButton.setBackground(Color.red.darker());
        this.resetButton.setFocusable(false);
        this.resetButton.setPreferredSize(new Dimension(150, 50));
        this.resetButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        this.resetButton.setForeground(Color.white);

        JPanel optionPanel = new JPanel();
        optionPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        optionPanel.setPreferredSize(new Dimension(160, 120));
        FlowLayout optionLayout = new FlowLayout();
        optionLayout.setHgap(1000);
        optionPanel.setLayout(optionLayout);
        optionPanel.add(this.boardFlipToggle);

        sidePanel = new JPanel();
        this.sidePanel.setPreferredSize(new Dimension(200, 100));
        this.sidePanel.setBackground(new Color(150, 150, 150));
        FlowLayout sideLayout = new FlowLayout(FlowLayout.CENTER);
        sideLayout.setHgap(1000);
        this.sidePanel.setLayout(sideLayout);
        this.sidePanel.add(this.timerPanel);
        this.sidePanel.add(optionPanel);

        if (!isOnline) { // ensures no crazy stuff can happen with these buttons... :P
            this.sidePanel.add(this.undoButton);
            this.sidePanel.add(this.resetButton);
        }

        JPanel gameContainer = new JPanel();
        gameContainer.setLayout(new BoxLayout(gameContainer, BoxLayout.X_AXIS));
        gameContainer.add(gamePanel);
        gameContainer.add(deadPanel);

        resetListeners();

        this.add(gameContainer);
        this.add(sidePanel, BorderLayout.WEST);
    }

    @Override
    public Dimension getPreferredSize() {
        Container parent = this.getParent();
        if (parent == null) {
            return new Dimension(0, 0);
        }
        return new Dimension(parent.getWidth(), parent.getHeight());
    }

    public void setTileColor(int x, int y, Piece selectedPiece, Graphics g) {
        if (selectedPiece.canCapture(x, y, selectedPiece)
                || selectedPiece.canMove(x, y, selectedPiece)) {
            if (gameBoard.testBoth(selectedPiece, x, y, selectedPiece.white, this)) {
                if (selectedPiece.canCapture(x, y, selectedPiece)) {
                    g.setColor(averageColors(g.getColor(), Color.red));
                } else if (selectedPiece.canMove(x, y, selectedPiece)) {
                    g.setColor(averageColors(g.getColor(), Color.yellow));
                }
            } else if (gameBoard.isBlocked(x, y, selectedPiece)) {
                g.setColor(averageColors(g.getColor(), Color.green));
            } else if (gameBoard.isTileInvalid(x, y, selectedPiece)) {
                g.setColor(averageColors(g.getColor(), Color.blue));
            } else {
                g.setColor(averageColors(g.getColor(), Color.darkGray));
            }
        }
    }

    public void extraBoardGraphics(int tileSize, int offX, int offY, Graphics g) { // to be overriden by extending
                                                                                   // classes
    }

    public static enum GameType {
        Classic("Classic", new Color(255, 80, 80)),
        Atomic("Atomic", Color.orange),
        AntiChess("Antichess", Color.yellow),
        KingOfTheHill("KotH", Color.green),
        ThreeCheck("Three Check", averageColors(Color.blue, Color.white)),
        DoubleMove("Double Move", new Color(255, 0, 255)),
        CrazyHouse("Crazyhouse", new Color(200, 150, 0));

        public final String text;
        public final Color theme;

        private GameType(String text, Color theme) {
            this.text = text;
            this.theme = theme;
        }
    }

    public static enum EndType {
        NONE("", false),
        CHECKMATE("Checkmate!", true),
        TIME("Time!", true),
        RESIGNATION("Resignation!", true),
        STALEMATE("Stalemate!", false),
        STALEMATEWIN("Stalemate!", true),
        VARIANT("Variant End!", true);

        public final String text;
        public final boolean win;

        private EndType(String text, boolean win) {
            this.text = text;
            this.win = win;
        }
    }

    public void selectTile(int x, int y, boolean rightClick) {
        if (gameBoard.validPos(x, y)) {
            if (selectedPiece == null) {
                if (gameBoard.getTile(x, y).getPiece() == null) {
                    return;
                }
                Piece piece = gameBoard.getTile(x, y).getPiece();
                if (piece.white != currentPlayer.isWhite()) {
                    return;
                }
                selectedPiece = piece;
            } else {
                Piece pieceToUse = selectedPiece;
                selectedPiece = null;
                if (allowMove) {
                    // System.out.println(pieceToUse.getID() + " " +
                    // gameBoard.getPieceByID(pieceToUse.getID()).getBasicName());
                    tryMove(pieceToUse, x, y, rightClick);
                }
            }
        }
        gamePanel.repaint();
    }

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

    public boolean hordeRulesApply(boolean white) {
        if (white) {
            return !whiteKingInit;
        } else {
            return !blackKingInit;
        }
    }

    public void addNewSave(int oldX, int oldY, int x, int y, Board oldBoard) {
        GameStateSave newSave = new GameStateSave(oldBoard, pastX, pastY, newX, newY, currentPlayer.isWhite(),
                whitePlayer.getTime(), blackPlayer.getTime());
        previousBoards.add(newSave);
    }

    public void advanceGame(int oldX, int oldY, int x, int y) {
        updatePastPositions(oldX, oldY, x, y);
        switchPlayer();
        startTimeCheck();
        updateWin();
        deadPanel.adjustInfo();
        if (isOnline) {
            Main.getClient().sendPacket(new UpdateGameRequest(this));
        }
    }

    public void updatePastPositions(int oldX, int oldY, int x, int y) {
        pastX = oldX;
        pastY = oldY;
        newX = x;
        newY = y;
    }

    public void undo() {
        if (previousBoards.size() <= 1) {
            return;
        }
        GameStateSave nextSave = previousBoards.remove(previousBoards.size() - 1);
        pastX = nextSave.oldX;
        pastY = nextSave.oldY;
        newX = nextSave.newX;
        newY = nextSave.newY;
        switchToPlayer(nextSave.whiteTurn);
        whitePlayer.setTime(nextSave.whiteTimeLeft);
        blackPlayer.setTime(nextSave.blackTimeLeft);
        gameBoard = nextSave.boardSave;
        this.timerPanel.setActive(false);
        startTimeCheck();

        selectedPiece = null;
        allowMove = true;
        updateWin();
        repaint();
        deadPanel.adjustInfo();
    }

    public void updateWin() {
        EndType whiteWinType = checkForWin(true);
        EndType blackWinType = checkForWin(false);
        whiteWin = whiteWinType.win;
        blackWin = blackWinType.win;

        if (whiteWinType != EndType.NONE || blackWinType != EndType.NONE) {
            this.timerPanel.setActive(false);
            this.allowMove = false;
        }

        if (whiteWinType != EndType.NONE) {
            this.winType = whiteWinType;
        } else {
            this.winType = blackWinType;
        }

        gamePanel.repaint();
    }

    public EndType checkForWin(boolean white) {
        if (!gameBoard.canPlayerMove(!white, true, this)) { // testing for checkmate
            if (!gameBoard.overallSafety(!white, this)) {
                return EndType.CHECKMATE;
            }
        }
        if (!gameBoard.canPlayerMove(!white, true, this)) { // testing for stalemate
            if (gameBoard.overallSafety(!white, this) && getCurrentPlayer().isWhite() != white) {
                return EndType.STALEMATE;
            }
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

    public void startTimeCheck() {
        if (this.gameBoard.turnNumber >= 2) {
            this.timerPanel.setActive(true);
        }
    }

    public void switchPlayer() {
        if (currentPlayer == whitePlayer) {
            currentPlayer = blackPlayer;
        } else {
            currentPlayer = whitePlayer;
        }
        this.timerPanel.switchPlayer();
    }

    public void switchToPlayer(boolean white) {
        this.timerPanel.switchPlayer(white);
        if (white) {
            this.currentPlayer = whitePlayer;
        } else {
            this.currentPlayer = blackPlayer;
        }
    }

    public void resetGame() {
        this.whiteWin = false;
        this.blackWin = false;
        this.winType = EndType.NONE;
        this.allowMove = true;
        this.pastX = -1;
        this.pastY = -1;
        this.newX = -1;
        this.newY = -1;
        this.gameBoard = this.originalBoard.mockBoard();
        this.currentPlayer = this.whitePlayer;
        this.selectedPiece = null;

        this.timerPanel.resetTime();
        this.previousBoards.clear();
        this.previousBoards.add(new GameStateSave(originalBoard.mockBoard(), pastX, pastY, newX, newY,
                currentPlayer.isWhite(), whitePlayer.getTime(), blackPlayer.getTime()));
        this.gamePanel.repaint();
        this.deadPanel.adjustInfo();
    }

    public void setReady() {
        this.ready = true;
        this.allowMove = true;
        revalidate();
        repaint();
    }

    public void setUntimed(boolean untimed) {
        this.timerPanel.setUntimed(untimed);
    }

    public void unselectPiece() {
        this.selectedPiece = null;
        repaint();
    }

    public static Color averageColors(Color col1, Color col2) {
        return new Color((col1.getRed() + col2.getRed()) / 2, (col1.getGreen() + col2.getGreen()) / 2,
                (col1.getBlue() + col2.getBlue()) / 2);
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public Long getID() {
        return this.ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public void setBoardFlip(boolean boardFlip) {
        this.boardFlip = boardFlip;
    }

    public boolean isOnline() {
        return this.isOnline;
    }

    public boolean doesAllowMove() {
        return this.allowMove;
    }

    public void resetListeners() {
        for (MouseListener ml : gamePanel.getMouseListeners()) {
            gamePanel.removeMouseListener(ml);
        }
        gamePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int reverseX = (!currentPlayer.isWhite() && boardFlip) ? sizeX : 0,
                        reverseY = (!currentPlayer.isWhite() && boardFlip) ? sizeY : 0;
                int posX = (Math.abs(reverseX - e.getX()) - offsetX) / Math.max(tileSize, 1);
                int posY = (Math.abs(reverseY - e.getY()) - offsetY) / Math.max(tileSize, 1);
                boolean rightClick = e.getButton() == 3;

                if (!isOnline) {
                    selectTile(posX, posY, rightClick);
                } else {
                    Main.getClient().sendPacket(new MoveRequest(posX, posY, rightClick, currentPlayer.isWhite()));
                }
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

        boardFlipToggle.setSelected(boardFlip);
        for (ActionListener al : boardFlipToggle.getActionListeners()) {
            boardFlipToggle.removeActionListener(al);
        }
        this.boardFlipToggle.addActionListener((ActionEvent e) -> {
            boardFlip = boardFlipToggle.isSelected();

            if (isOnline) {
                Main.getClient().setBoardFlip(boardFlip);
            }

            gamePanel.repaint();
        });

        for (ActionListener al : undoButton.getActionListeners()) {
            undoButton.removeActionListener(al);
        }
        this.undoButton.addActionListener((ActionEvent e) -> {
            undo();
        });

        for (ActionListener al : resetButton.getActionListeners()) {
            resetButton.removeActionListener(al);
        }
        this.resetButton.addActionListener((ActionEvent e) -> {
            resetGame();
        });

        this.timerPanel.resetInstance();
        this.deadPanel.resetInstance();
    }

    public GameStateSave[] getCurrentSaves() {
        GameStateSave[] saves = new GameStateSave[this.previousBoards.size()];
        for (int i = 0; i < this.previousBoards.size(); i++) {
            saves[i] = this.previousBoards.get(i);
        }
        return saves;
    }
}