package Chess.Pieces;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

import Chess.BoardStuff.*;
import Chess.Games.Variant;

public abstract class Piece implements Serializable {
    protected Board board;
    protected Variant game;
    public int x, y; //rank and file
    public int value = 0;
    public boolean initMove = true, seesDead = false, countsForAll = true, kingLike = false, white;
    public String description = "";
    public ImageIcon image, deadImage;
    public abstract boolean canMove(int x, int y, Piece piece);
    public abstract boolean canCapture(int x, int y, Piece piece);
    private static final Map<String, ImageIcon[]> pieceImages = new HashMap<>();
    private static final Map<String, String> pieceDescriptions = new HashMap<>();
    private static final Map<String, Integer> pieceValues = new HashMap<>();
    public static final String[] nameList;
    private static long currentID = 0;
    private long ID;

    public Piece(Variant game, int x, int y, boolean color) {
        this.game = game;
        if (game != null) {
            this.board = game.gameBoard;
        }
        this.x = x;
        this.y = y;
        this.white = color;
        this.ID = currentID;
        currentID++;
        init();
    }

    public Piece copy(Board newBoard, boolean keepID) {
        Piece returnPiece;
        try {
            returnPiece = this.getClass().getDeclaredConstructor(new Class[] {Variant.class, int.class, int.class, boolean.class}).newInstance(this.game, this.x, this.y, this.white);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            return null;
        }
        returnPiece.initMove = this.initMove;
        returnPiece.setBoard(newBoard);
        if (keepID) {
            returnPiece.ID = this.ID;
            currentID--;
        }
        return getClass().cast(returnPiece);
    }

    static {
        nameList = new String[] { "achilles", "amazon", "bishop", "blocker", "bomber", "cannon", "copycat", "hercules",
                "hunter", "king", "kingpawn", "knight", "knightrider", "leaper", "neobishop", "neorook", "pawn",
                "priest", "queen", "rook", "schizophrenic", "switcher", "treasonist", "rewinder" };
        int[] valueList = { 7, 9, 3, 9, 9, 9, 9, 9, 9, 0, 0, 3, 5, 5, 5, 7, 1, 7, 9, 5, 5, 5, 4, 5 };
        for (int i = 0; i < nameList.length; i++) {
            String name = nameList[i];
            int value = valueList[i];
            initImages(name);
            initValue(name, value);
        }
        initDescription("achilles",
                "The Achilles: Much like the myth, he's invincible!! Oh... except for when they're farther than a square away... He moves like a King.");
        initDescription("amazon", "The Amazon: Moves like a Queen and a Knight, COMBINED!!");
        initDescription("bishop",
                "The Bishop: A Classic Chess piece, it will never leave its initial color of square, as it moves any number of unobstructed spaces diagonally.");
        initDescription("blocker",
                "The Blocker: This piece has an activated ability! While active, it disallows the movement of any piece through or onto the squares surrounding it (except for the square that its on!). Also it moves like a Knight and Rook combined.");
        initDescription("bomber",
                "The Bomber: An explosive approach, it moves like a queen, but when capturing, will explode all pieces in a 3x3 radius, even Kings!");
        initDescription("cannon",
                "The Cannon: A tactical fighter, the cannon only moves as a King would, but it can kill pieces in the pattern of a Queen, staying in its place.");
        initDescription("copycat",
                "The CopyCat: A very tricky piece, it will move like the piece your opponent played on the turn previous, along with have all of its abilities!");
        initDescription("hercules",
                "The Hercules: This piece is an odd one, it only has 10 total moves, but are they powerful... you might want to test them for yourself!");
        initDescription("hunter",
                "The Hunter: Not so powerful at first, this piece initially moves like a Knight... but when it captures a piece, it steals all of its moves and abilities, and yes, this stacks >:).");
        initDescription("king",
                "The King: The most important piece of the Classic Chess game, when it has nowhere left to go, you lose. It moves and captures 1 space in any direction, as long as it is a safe, unobstructed square.");
        initDescription("kingpawn",
                "The KingPawn: Just like the King, if it has no escape, you lose. Yet it's even weaker! Have fun protecting this guy, who only moves like a pawn.");
        initDescription("knight",
                "The Knight: Another tactical piece, the Knight can only move in jumps of L-Shapes, specifically 1-2 or 2-1. What the fork??");
        initDescription("knightrider",
                "The KnightRider: This piece, like the Knight, moves in L-Shapes... but it can keep hopping in the same direction as long as it's not blocked! Have fun travelling the whole board in one move!");
        initDescription("leaper",
                "The Leaper: An interesting fellow, the Leaper can hop horizontally or vertically over pieces, by as many pieces as there are around it...");
        initDescription("neobishop",
                "The NeoBishop: This piece moves like the Bishop, except for being able to phase through exactly 1 piece on its travel!");
        initDescription("neorook",
                "The NeoRook: This piece moves like the Rook, except for being able to phase through exactly 1 piece on its travel!");
        initDescription("pawn",
                "The Pawn: Not the most exciting piece in Classic Chess, it can move 1 or 2 forwards on its first turn, and otherwise just 1. Be careful, though, it only captures diagonally forward!");
        initDescription("priest",
                "The Priest: Sworn to holiness, this piece cannot kill, yet still offers crazy benefits! It moves like a Knight or a Switcher, and can also revive any of your dead pieces, from anywhere on the map (Even the King!!).");
        initDescription("queen",
                "The Queen: The most powerful piece in Classic Chess, she can move like a rook and bishop combined!");
        initDescription("rook",
                "The Rook: A Classic Chess piece, the Rook can move horizontally or vertically any number of unobstructed spaces! Did someone say 'Ladder Mate?'");
        initDescription("schizophrenic",
                "The Schizophrenic: An unpredictable one, this guy can move like a Knight, or a Bishop, or a Rook, or a Pawn? Kinda depends on his mood though...");
        initDescription("switcher",
                "The Switcher: A unique piece, it can only do one thing, which is what its name implies... switching places with your own pieces.");
        initDescription("treasonist",
                "The Treasonist: Now, I know what you might be thinking, why would I want to kill my own pieces? Good question, this guy does it though. It moves like a rook.");
    }
    
    public final void init() {
        setImages();
        setDescription();
        setValue();
    }

    //Whenever pieces are created, they call these functions (within the init() function) to give them their values
    public final void setImages() {
        int index = 0;
        if (!this.white) {
            index += 2;
        }
        ImageIcon[] currentImages = pieceImages.get(getBasicName());
        this.image = currentImages[index];
        this.deadImage = currentImages[index + 1];
    }

    public final void setDescription() {
        this.description = pieceDescriptions.get(getBasicName());
    }

    public final void setValue() {
        this.value = pieceValues.get(getBasicName());
    }

    public final long getID() {
        return this.ID;
    }

    public final void setID(long ID) {
        this.ID = ID;
    }

    public final void setGame(Variant game) {
        this.game = game;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return this.board;
    }

    public boolean opposingPieces(Piece other) {
        return this.white != other.white;
    }

    public static boolean opposingPieces(Piece p1, Piece p2) {
        return p1.white != p2.white;
    }

    public boolean inDanger() {
        boolean danger = false;
        for (Piece piece : this.board.pieces) {
            danger |= piece.canCapture(this.x, this.y, piece);
        }
        return danger;
    }

    public boolean preMoveCheck(int x, int y) {
        return true;
    }

    public boolean canBeCaptured(int oldX, int oldY, int x, int y, Piece attacker) {
        return opposingPieces(attacker);
    }

    public void specialMoveEvent(int oldX, int oldY, int x, int y, Piece captured, boolean rightClick, Piece piece) { //for the purpose of moves like the shooter, where it doesn't actually move, to be checked in the move checking process
    }

    public boolean invalidateTile(int x, int y, Piece attacker) { //for the purpose of making some pieces disallow the movement of pieces to be close to them
        return false;
    }

    public boolean blockTile(int x, int y, Piece attacker) {
        return false;
    }

    public void onTurnEnd(Piece movedPiece, int oldX, int oldY, int x, int y, boolean white) { //passive method that occurs for every piece after every turn
    }

    public void specialGraphics(Graphics g, int tileSize, int offX, int offY, boolean selected) {
    }

    //Initializing all of the unchanging piece data here
    public static void initImages(String name) {
        ImageIcon[] imgList = new ImageIcon[4];
        imgList[0] = new ImageIcon(
                getResource(getLink(name, true, false)));
        imgList[1] = new ImageIcon(
                getResource(getLink(name, true, true)));
        imgList[2] = new ImageIcon(
                getResource(getLink(name, false, false)));
        imgList[3] = new ImageIcon(
                getResource(getLink(name, false, true)));
        pieceImages.put(name, imgList);
    }
    public static void initDescription(String name, String text) {
        pieceDescriptions.put(name, text);
    }
    public static void initValue(String name, int value) {
        pieceValues.put(name, value);
    }

    public final String getBasicName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public static BufferedImage getResource(String name) {
        try {
            return ImageIO.read(Piece.class.getClassLoader().getResourceAsStream(name));
        } catch (IOException e) {
            return null;
        }
    }

    public static String getLink(String name, boolean white, boolean dead) {
        return "resources/" + ((dead) ? "dead-" : "") + ((white) ? "white-" : "black-")
                + name.toLowerCase() + ".png";
    }
}