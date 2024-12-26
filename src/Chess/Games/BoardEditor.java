package Chess.Games;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

import Chess.Main;
import Chess.BoardStuff.*;
import Chess.Games.Variant.GameType;
import Chess.Pieces.*;
import Chess.UI.PieceButton;
import Chess.UI.SetupMenuItem;
import Chess.UI.VariantMenuItem;
import Chess.UI.Windows.GameWindow;
import Online.Client;
import Online.Packets.CreateGameRequest;

public class BoardEditor extends JFrame {
    public final static Piece[] pieceList;
    private final static Color MAROON__COLOR, TAN__COLOR;
    private final static Font buttonFont = new Font(Font.MONOSPACED, Font.BOLD, 15);
    private final JPanel sidePanel, boardPanel, piecePanel;
    private final JScrollPane pieceScroller;
    private final JTextField sizeField;
    private final JMenu variantMenu, setupMenu;
    private final JButton gameButton, onlineGameButton, clearButton, color1Button, color2Button;
    private final Board board;

    private PieceButton selectedButton = null;
    private int sizeX, sizeY, offsetX, offsetY, tileSize;
    private boolean timedGame = false;
    private GameType selectedGameType = GameType.Classic;

    static {
        MAROON__COLOR = new Color(200, 100, 100);
        TAN__COLOR = new Color(230, 200, 150);
        pieceList = new Piece[] {
                null,
                new Pawn(null, 0, 0, true),
                new Knight(null, 0, 0, true),
                new Bishop(null, 0, 0, true),
                new Rook(null, 0, 0, true),
                new Queen(null, 0, 0, true),
                new King(null, 0, 0, true),
                new Amazon(null, 0, 0, true),
                new KnightRider(null, 0, 0, true),
                new Bomber(null, 0, 0, true),
                new Blocker(null, 0, 0, true),
                new Schizophrenic(null, 0, 0, true),
                new Cannon(null, 0, 0, true),
                new CopyCat(null, 0, 0, true),
                new Priest(null, 0, 0, true),
                new Hunter(null, 0, 0, true),
                new Treasonist(null, 0, 0, true),
                new NeoRook(null, 0, 0, true),
                new NeoBishop(null, 0, 0, true),
                new KingPawn(null, 0, 0, true),
                new Leaper(null, 0, 0, true),
                new Achilles(null, 0, 0, true),
                new Rewinder(null, 0, 0, true)
        };
    }

    public BoardEditor() {
        this.setTitle("Chess Editor");
        this.board = new Board();
        this.board.col1 = MAROON__COLOR;
        this.board.col2 = TAN__COLOR;
        this.board.setUpClassicGame();
        this.boardPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                // getting all the numbers required to scale the screen properly :)
                Rectangle rect = boardPanel.getBounds();
                sizeX = (int) rect.getWidth();
                sizeY = (int) rect.getHeight();
                int smallestSize = Math.min(sizeX, sizeY);
                int biggestDimension = Math.max(board.width, board.height);
                tileSize = smallestSize / biggestDimension;
                offsetX = (sizeX - tileSize * board.width) / 2;
                offsetY = (sizeY - tileSize * board.height) / 2;
                int flipOffsetX = 0;
                int flipOffsetY = 0;

                // draw the background
                g.setColor(Color.gray);
                g.fillRect(0, 0, sizeX, sizeY);

                for (int x = 0; x < board.width; x++) {
                    for (int y = 0; y < board.height; y++) {
                        int xPos = Math.abs(flipOffsetX - x * tileSize) + offsetX,
                                yPos = Math.abs(flipOffsetY - y * tileSize) + offsetY;

                        // coloring of the tiles for indicators
                        if (x % 2 == y % 2) {
                            g.setColor(board.col1);
                        } else {
                            g.setColor(board.col2);
                        }
                        g.fillRect(xPos, yPos, tileSize, tileSize);

                        // drawing the pieces to the screen after all the tiles are done
                        if (board.isPieceAt(x, y)) {
                            Piece piece = board.getTile(x, y).getPiece();
                            g.drawImage(piece.image.getImage(), xPos, yPos, tileSize, tileSize, null);
                        }
                    }
                }
            }
        };
        this.boardPanel.setPreferredSize(new Dimension(800, 800));
        this.boardPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int posX = (e.getX() - offsetX) / Math.max(tileSize, 1);
                int posY = (e.getY() - offsetY) / Math.max(tileSize, 1);
                boolean rightClick = e.getButton() == 3;
                selectTile(posX, posY, rightClick);
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
        this.add(this.boardPanel);

        this.sizeField = new JTextField();
        this.sizeField.setPreferredSize(new Dimension(100, 30));
        this.sizeField.setToolTipText("Input Dimensions for Board (separate with spaces)");
        this.sizeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validForDimensions(sizeField.getText())) {
                    int[] dims = getDimensions(sizeField.getText());
                    newBoard(dims[0], dims[1]);
                }
                repaint();
            }
        });

        this.gameButton = new JButton("Play");
        this.gameButton.setFont(buttonFont);
        this.gameButton.setPreferredSize(new Dimension(150, 50));
        this.gameButton.setBackground(Color.green);
        this.gameButton.setFocusable(false);
        this.gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Variant newGame = getGame(false);
                new GameWindow(newGame);
            }
        });

        this.onlineGameButton = new JButton("Play Online");
        this.onlineGameButton.setFont(buttonFont);
        this.onlineGameButton.setPreferredSize(new Dimension(150, 50));
        this.onlineGameButton.setBackground(Color.green.darker());
        this.onlineGameButton.setFocusable(false);
        this.onlineGameButton.addActionListener(e -> {
            Client client = Main.getClient();
            if (client.getValid()) {
                boolean white;
                int selection = JOptionPane.showConfirmDialog(this, "Play as white?");
                if (selection == 0) {
                    white = true;
                } else if (selection == 1) {
                    white = false;
                } else {
                    return;
                }

                Variant newGame = getGame(true);
                client.sendPacket(new CreateGameRequest(newGame, white));
            } else {
                client.runClient();
            }
        });

        JCheckBox timeBox = new JCheckBox("Use Timer?");
        timeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timedGame = timeBox.isSelected();
            }
        });
        timeBox.setFocusable(false);
        timeBox.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        timeBox.setBackground(Color.gray.brighter());
        timeBox.setForeground(Color.black);
        timeBox.setPreferredSize(new Dimension(150, 50));

        this.piecePanel = new JPanel();
        this.piecePanel.setLayout(new GridLayout(pieceList.length, 1));
        for (Piece piece : pieceList) {
            PieceButton button = new PieceButton(piece);
            if (piece instanceof Pawn) {
                selectedButton = button;
            }
            makePieceButton(button);
            this.piecePanel.add(button);
        }
        colorButtons(null);
        this.pieceScroller = new JScrollPane(this.piecePanel);
        this.pieceScroller.setPreferredSize(new Dimension(150, 300));
        this.pieceScroller.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                pieceScroller.getVerticalScrollBar()
                        .setValue(pieceScroller.getVerticalScrollBar().getValue() + e.getUnitsToScroll() * 20);
            }
        });

        this.clearButton = new JButton("Clear Board");
        makeUiButton(this.clearButton, Color.red, 15);
        this.clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newBoard(board.width, board.height);
                boardPanel.repaint();
            }
        });

        this.color1Button = new JButton();
        this.color1Button.setFocusable(false);
        this.color1Button.setBackground(board.col1);
        this.color1Button.setPreferredSize(new Dimension(75, 50));
        this.color1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(boardPanel, "Pick a color!", board.col1);
                if (selectedColor != null) {
                    color1Button.setBackground(selectedColor);
                    board.col1 = selectedColor;
                    boardPanel.repaint();
                }
            }
        });

        this.color2Button = new JButton();
        this.color2Button.setFocusable(false);
        this.color2Button.setBackground(board.col2);
        this.color2Button.setPreferredSize(new Dimension(75, 50));
        this.color2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(boardPanel, "Pick a color!", board.col2);
                if (selectedColor != null) {
                    color2Button.setBackground(selectedColor);
                    board.col2 = selectedColor;
                    boardPanel.repaint();
                }
            }
        });

        JPanel colorSelectorPanel = new JPanel(new GridLayout(1, 2));
        colorSelectorPanel.add(this.color1Button);
        colorSelectorPanel.add(this.color2Button);

        JMenuBar variantBar = new JMenuBar();
        variantBar.setOpaque(true);
        variantMenu = new JMenu("Variants") {
            @Override
            public void paintComponent(Graphics g) {
                String text = selectedGameType.text + " Mode";
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
                Rectangle2D textBounds = g.getFontMetrics().getStringBounds(text, g);
                Rectangle bounds = variantMenu.getBounds();
                int width = (int) bounds.getWidth(), height = (int) bounds.getHeight(),
                        tWidth = (int) textBounds.getWidth();
                g.setColor(selectedGameType.theme);
                g.fillRect(0, 0, width, height);
                g.setColor(Color.black);
                g.drawString(text, width - (width + tWidth) / 2, 30);
            }
        };
        variantMenu.setPreferredSize(new Dimension(150, 50));
        variantBar.add(variantMenu);
        for (GameType gT : GameType.values()) {
            VariantMenuItem newItem = new VariantMenuItem(gT);
            newItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedGameType = newItem.getType();
                }
            });
            variantMenu.add(newItem);
        }

        JMenuBar setupBar = new JMenuBar();
        setupBar.setOpaque(true);
        setupMenu = new JMenu("Setups") {
            @Override
            public void paintComponent(Graphics g) {
                String text = "Setups";
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
                Rectangle2D textBounds = g.getFontMetrics().getStringBounds(text, g);
                Rectangle bounds = variantMenu.getBounds();
                int width = (int) bounds.getWidth(), height = (int) bounds.getHeight(),
                        tWidth = (int) textBounds.getWidth();
                g.setColor(Color.orange);
                g.fillRect(0, 0, width, height);
                g.setColor(Color.black);
                g.drawString(text, width - (width + tWidth) / 2, 30);
            }
        };
        setupMenu.setPreferredSize(new Dimension(150, 50));
        setupBar.add(setupMenu);
        for (int i = 0; i < 3; i++) {
            final int assignment = i;
            SetupMenuItem newItem = new SetupMenuItem(this.board, assignment);
            newItem.addActionListener((ActionEvent e) -> {
                SetupMenuItem.getSetup(board, assignment).setup();
                repaint();
            });
            setupMenu.add(newItem);
        }

        this.sidePanel = new JPanel();
        this.sidePanel.setPreferredSize(new Dimension(200, 1));
        this.sidePanel.add(this.gameButton);
        this.sidePanel.add(this.onlineGameButton);
        this.sidePanel.add(variantBar);
        this.sidePanel.add(timeBox);
        this.sidePanel.add(this.sizeField);
        this.sidePanel.add(this.pieceScroller);
        this.sidePanel.add(this.clearButton);
        this.sidePanel.add(setupBar);
        this.sidePanel.add(colorSelectorPanel);
        this.add(this.sidePanel, BorderLayout.WEST);

        this.pack();
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    public Variant getGame(boolean online) {
        Board newBoard = board.mockBoard();
        Variant newGame;
        switch (selectedGameType) {
            case AntiChess:
                newGame = new AntiChessGame(newBoard, online);
                break;
            case Atomic:
                newGame = new AtomicGame(newBoard, online);
                break;
            case Classic:
                newGame = new ClassicGame(newBoard, online);
                break;
            case KingOfTheHill:
                newGame = new KingOfTheHillGame(newBoard, online);
                break;
            case ThreeCheck:
                newGame = new ThreeCheckGame(newBoard, online);
                break;
            case DoubleMove:
                newGame = new DoubleMoveGame(newBoard, online);
                break;
            case CrazyHouse:
                newGame = new CrazyHouseGame(newBoard, online);
                break;
            default:
                newGame = new ClassicGame(newBoard, online);
                break;
        }
        newGame.setUntimed(!timedGame);
        return newGame;
    }

    public void selectTile(int x, int y, boolean rightClick) {
        if (this.board.validPos(x, y)) {
            Piece newPiece = null;
            if (selectedButton != null) {
                if (selectedButton.getPiece() != null) {
                    if (rightClick) {
                        newPiece = selectedButton.getPiece().copy(this.board, false);
                        newPiece.x = x;
                        newPiece.y = y;
                        newPiece.white = false;
                        newPiece.setImages();
                    } else {
                        newPiece = selectedButton.getPiece().copy(this.board, false);
                        newPiece.x = x;
                        newPiece.y = y;
                        newPiece.white = true;
                        newPiece.setImages();
                    }
                }
            }
            if (newPiece != null) {
                this.board.addToBoard(newPiece, false);
            } else {
                this.board.removePiece(this.board.getTile(x, y).getPiece());
            }
        }
        boardPanel.repaint();
    }

    public boolean validForDimensions(String text) {
        String[] test = text.replace(",", "").split(" ");
        if (test.length == 2) {
            for (int i = 0; i < 2; i++) {
                try {
                    Integer.valueOf(test[i]);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int[] getDimensions(String text) {
        int[] returnDims = new int[2];
        String[] nums = text.replace(",", "").split(" ");
        for (int i = 0; i < 2; i++) {
            returnDims[i] = Integer.parseInt(nums[i]);
        }
        return returnDims;
    }

    public void newBoard(int width, int height) {
        BoardSetup clear = new BoardSetup(this.board, width, height);
        clear.setup();
    }

    public void makeUiButton(JButton button, Color color, int fontSize) {
        button.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        button.setPreferredSize(new Dimension(150, 50));
        button.setBackground(color);
        button.setFocusable(false);
    }

    public void makePieceButton(PieceButton button) {
        button.setPreferredSize(new Dimension(100, 100));
        if (button.getPiece() != null) {
            button.setIcon(button.getPiece().image);
        } else {
            button.setIcon(null);
        }
        button.setBackground(Color.gray);
        button.addActionListener((ActionEvent e) -> {
            colorButtons(button);
        });
    }

    public void colorButtons(PieceButton activated) {
        if (activated != null) {
            selectedButton = activated;
        }
        for (Component component : piecePanel.getComponents()) {
            if (component == selectedButton) {
                component.setBackground(Color.yellow);
            } else {
                component.setBackground(Color.gray);
            }
        }
        repaint();
    }
}