package Chess.Games.UI;

import javax.swing.JMenuItem;

import Chess.Games.Variant.GameType;

import java.awt.Dimension;

public class VariantMenuItem extends JMenuItem {
    private GameType gameType;
    
    public VariantMenuItem(GameType gT) {
        this.gameType = gT;
        this.setBackground(gT.theme);
        this.setText(gT.name());
        this.setPreferredSize(new Dimension(150, 20));
    }

    public GameType getType() {
        return this.gameType;
    }
}
