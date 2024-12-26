package Chess.Games.UI.Windows;
import Chess.Main;
import Chess.Games.BoardEditor;
import Chess.Pieces.*;
import Online.Client;
import Online.Packets.JoinGameWindowRequest;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ChessWindow extends JFrame {
    private JPanel menuPanel, UiPanel;
    private JButton makeGameButton, joinGameButton;
    private int imageSize = 200;
    private Color bgColor = Color.red;

    public ChessWindow() {
        super("Austin Chess :)");

        BufferedImage pieceImage = Piece.getResource("resources/white-copycat.png");
        menuPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Rectangle rect = menuPanel.getBounds();
                int sizeX = (int) rect.getWidth();
                int sizeY = (int) rect.getHeight();
                g.setColor(bgColor);
                g.fill3DRect(0, 0, sizeX, sizeY, true);
                g.setColor(Color.yellow);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD | Font.ITALIC, 50));
                g.drawString("Austin Chess", 75, 50);
                g.drawImage(pieceImage, (sizeX - imageSize) / 2, (sizeY - imageSize) / 2, imageSize, imageSize, null);
            }
        };
        menuPanel.setPreferredSize(new Dimension(500, 500));

        makeGameButton = new JButton("Create Game");
        makeGameButton.setPreferredSize(new Dimension(150, 50));
        makeGameButton.addActionListener(e -> {
            BoardEditor editor = new BoardEditor();
            editor.setVisible(true);
        });

        joinGameButton = new JButton("Join Game");
        joinGameButton.setPreferredSize(new Dimension(150, 50));
        joinGameButton.addActionListener(e -> {
            Client client = Main.getClient();
            if (client.getValid()) {
                client.sendPacket(new JoinGameWindowRequest());
            } else {
                client.runClient();
            }
        });

        UiPanel = new JPanel();
        UiPanel.add(makeGameButton);
        UiPanel.add(joinGameButton);

        add(menuPanel);
        add(UiPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
            }
            @Override
            public void windowClosed(WindowEvent e) {
                Main.getClient().shutdown();
                System.exit(1);
            }
            @Override
            public void windowIconified(WindowEvent e) {
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            @Override
            public void windowActivated(WindowEvent e) {
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        Runnable colorRunner = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                    double seconds = System.currentTimeMillis() / 1000.0;
                    imageSize = (int) (Math.sin(seconds * 4.0) * 75) + 300;
                    bgColor = Color.getHSBColor((float) ((seconds / 5.0 - Math.floor(seconds / 5.0))), 1, 1);
                    repaint();
                }
            }
        };
        colorRunner.run();
    }

}