package Chess.UI;

import java.awt.*;
import javax.swing.*;

import Chess.BoardStuff.*;

public class SetupMenuItem extends JMenuItem {
    public final static String[] names;
    public final static Color[] colors;

    static {
        names = new String[] {"Classic", "Chess960", "Horde"};
        colors = new Color[] {Color.cyan, Color.yellow, Color.red};
    }

    public SetupMenuItem(Board board, int assignment) {
        String name = "";
        Color color = Color.gray;
        if (assignment >= 0 && assignment < names.length) {
            name = names[assignment];
            color = colors[assignment];
        }
        this.setBackground(color);
        this.setText(name);
        this.setPreferredSize(new Dimension(150, 20));
    }

    public static BoardSetup getSetup(Board board, int assignment) {
        switch (assignment) {
            case 0:
                return BoardSetup.generateClassicBoard(board);
            case 1:
                return BoardSetup.generate960Board(board);
            case 2:
                return BoardSetup.generateHordeSetup(board);
            default:
                return BoardSetup.generateClassicBoard(board);
        }
    }
}
