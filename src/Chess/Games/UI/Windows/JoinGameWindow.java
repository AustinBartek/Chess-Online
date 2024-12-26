package Chess.Games.UI.Windows;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Chess.Games.Variant;

public class JoinGameWindow extends JFrame {
    private final JPanel joinableGamesPanel;
    private final JScrollPane scrollView;

    public JoinGameWindow(HashMap<String, Variant> gamesInfo) {
        joinableGamesPanel = new JPanel();
        joinableGamesPanel.setLayout(new GridBagLayout());
        
        refreshUsernames(gamesInfo);
        scrollView = new JScrollPane(joinableGamesPanel);
        scrollView.setPreferredSize(new Dimension(400, 400));

        add(scrollView);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    public void refreshUsernames(HashMap<String, Variant> gamesInfo) {
        joinableGamesPanel.removeAll();
        for (String username : gamesInfo.keySet()) {
            JPanel joinableGame = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
                    g.drawString(username, 5, 20);
                }
            };
            joinableGame.setPreferredSize(new Dimension(400, 100));
            joinableGamesPanel.add(joinableGame);
        }

        repaint();
    }
}