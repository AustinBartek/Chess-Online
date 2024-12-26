package Chess.UI.Windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import Chess.Games.Variant;
import Chess.UI.JoinGamePanel;

public class JoinGameWindow extends JFrame {
    private final JPanel joinableGamesPanel;
    private final JScrollPane scrollView;

    public JoinGameWindow(HashMap<String, Variant> gamesInfo) {
        joinableGamesPanel = new JPanel();
        joinableGamesPanel.setLayout(new BoxLayout(joinableGamesPanel, BoxLayout.Y_AXIS));
        joinableGamesPanel.setBackground(Color.blue);
        
        refreshUsernames(gamesInfo);
        scrollView = new JScrollPane(joinableGamesPanel);
        scrollView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollView.setPreferredSize(new Dimension(600, 400));
        scrollView.addMouseWheelListener(e -> {
            scrollView.getVerticalScrollBar()
                    .setValue(scrollView.getVerticalScrollBar().getValue() + e.getUnitsToScroll() * 10);
        });

        add(scrollView);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    public void refreshUsernames(HashMap<String, Variant> gamesInfo) {
        joinableGamesPanel.removeAll();
        JPanel buttonHolder = new JPanel();
        buttonHolder.setLayout(new GridLayout(0, 1, 0, 5));
        buttonHolder.setBackground(Color.blue);
        int count = gamesInfo.size();
        for (String username : gamesInfo.keySet()) {
            JoinGamePanel joinableGame = new JoinGamePanel(username, gamesInfo.get(username));
            buttonHolder.add(joinableGame);
        }
        buttonHolder.setPreferredSize(new Dimension(400, count*100));
        buttonHolder.setMaximumSize(new Dimension(400, count*100));
        joinableGamesPanel.add(Box.createVerticalGlue());
        joinableGamesPanel.add(buttonHolder);
        joinableGamesPanel.add(Box.createVerticalGlue());

        repaint();
    }
}