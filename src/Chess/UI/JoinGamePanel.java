package Chess.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

import Chess.Main;
import Chess.Games.Variant;
import Chess.Games.Variant.GameType;
import Online.Client;
import Online.Packets.JoinGameRequest;

public class JoinGamePanel extends JButton {
    private static Font font = new Font(Font.MONOSPACED, Font.BOLD, 15);
    private String username;
    private Variant game;

    public JoinGamePanel(String name, Variant game) {
        this.username = name;
        this.game = game;

        this.addActionListener(e -> {
            Client client = Main.getClient();
            if (!client.canJoinGame()) {
                JOptionPane.showMessageDialog(null, "Cannot join a game! You are already trying to join, or are in a game!");
                return;
            }
            boolean confirm = JOptionPane.showConfirmDialog(null, "Join this game?") == 0;
            if (confirm) {
                client.setPendingGame(true);
                client.sendPacket(new JoinGameRequest(this.game));
                ((JFrame) getTopLevelAncestor()).dispose();
            }
        });

        setPreferredSize(new Dimension(400, 100));
        setMaximumSize(new Dimension(400, 100));
        setBorder(BorderFactory.createLoweredBevelBorder());
    }

    public Variant getGame() {
        return this.game;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = this.getWidth(), height = this.getHeight();

        GameType type = this.game.getGameType();
        String typeText = type.text, sizeX = this.game.gameBoard.width + "", sizeY = this.game.gameBoard.height + "";
        Color typeColor = type.theme;

        g.setColor(Color.lightGray);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.black);
        g.setFont(font);
        g.drawString(username, 5, 20);
        g.drawLine(4, 22, 395, 22);

        g.setColor(Color.black);
        g.fillRect(4, 30, 392, 60);

        g.setColor(typeColor);
        String totalText = typeText + " " + sizeX + "x" + sizeY;
        fitFontSize(g, totalText, 380, 60);
        int textHeight = 85 - (int) g.getFontMetrics().getStringBounds(totalText, g).getMaxY();
        g.drawString(totalText, 10, textHeight);
    }

    private void fitFontSize(Graphics g, String text, int width, int height) {
        Font newFont = font.deriveFont(1f);
        FontMetrics metrics = g.getFontMetrics(newFont);
        float currentSize = 1;

        while (metrics.stringWidth(text) < width && metrics.getStringBounds(text, g).getHeight() < height) {
            currentSize++;
            newFont = newFont.deriveFont(currentSize);
            metrics = g.getFontMetrics(newFont);
        }

        currentSize--;
        newFont = newFont.deriveFont(currentSize);
        g.setFont(newFont);
    }
}
