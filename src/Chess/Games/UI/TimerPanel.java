package Chess.Games.UI;

import java.text.DecimalFormat;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

import Chess.Games.Variant;
import Chess.Player.Player;

public class TimerPanel extends JPanel {
    private static DecimalFormat timeFormat = new DecimalFormat("##.#");
    static {
        timeFormat.setMinimumIntegerDigits(2);
        timeFormat.setMinimumFractionDigits(1);
    }
    private Player whitePlayer, blackPlayer, currentPlayer;
    private boolean ticking = false, doTime = true;
    private long oldTime;
    private double initTime;
    private SerializableThread timeThread;
    private Variant game;

    {
        oldTime = System.currentTimeMillis();
        Runnable tickTime = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    repaint();
                    if (ticking && doTime) {
                        updateTime();
                        checkTimeLeft();
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        this.timeThread = new SerializableThread(tickTime);

        Dimension panelSize = new Dimension(100, 50);

        JPanel whitePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                paintTime(g, whitePlayer.getTime(), Color.white);
            }
        };
        whitePanel.setPreferredSize(panelSize);

        JPanel blackPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                paintTime(g, blackPlayer.getTime(), Color.black);
            }
        };
        blackPanel.setPreferredSize(panelSize);

        JPanel playerPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Rectangle bounds = this.getBounds();
                int width = (int) bounds.getWidth(), height = (int) bounds.getHeight();
                boolean white = currentPlayer.isWhite();
                String message = ((white) ? "WHITE" : "BLACK");
                g.setColor((white) ? Color.white : Color.black);
                g.fillRect(0, 0, width, height);
                g.setColor((white) ? Color.black : Color.white);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
                Rectangle2D textBounds = g.getFontMetrics().getStringBounds(message, g);
                int textWidth = (int) textBounds.getWidth();
                g.drawString(message, width - (width / 2 + textWidth / 2), 22);
            }
        };
        playerPanel.setPreferredSize(new Dimension(100, 30));

        JPanel fillerPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 2; y++) {
                        if (x % 2 == y % 2) {
                            g.setColor(Color.gray);
                        } else {
                            g.setColor(Color.lightGray);
                        }
                        g.fillRect(x * 10, y * 10, 10, 10);
                    }
                }
            }
        };
        fillerPanel.setPreferredSize(new Dimension(100, 20));

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(whitePanel);
        containerPanel.add(blackPanel);
        containerPanel.add(fillerPanel);
        containerPanel.add(playerPanel);

        this.add(containerPanel);
    }

    public TimerPanel(Variant game) {
        this.game = game;
        this.whitePlayer = game.getWhitePlayer();
        this.blackPlayer = game.getBlackPlayer();
        this.currentPlayer = game.getCurrentPlayer();
        this.initTime = this.whitePlayer.getTime();
        this.timeThread.start();
    }

    public void setActive(boolean active) {
        this.ticking = active;
        oldTime = System.currentTimeMillis();
    }

    public void switchPlayer() {
        if (this.currentPlayer == this.whitePlayer) {
            this.currentPlayer = this.blackPlayer;
        } else {
            this.currentPlayer = this.whitePlayer;
        }
    }

    public void switchPlayer(boolean white) {
        if (white) {
            this.currentPlayer = this.whitePlayer;
        } else {
            this.currentPlayer = this.blackPlayer;
        }
    }

    public void paintTime(Graphics g, double time, Color bg) {
        String timeMessage = "";
        int minutes = (int) (time / 60);
        time -= minutes * 60;
        if (doTime) {
            timeMessage = minutes + ":" + timeFormat.format(Math.abs(time));
        } else {
            timeMessage = "No Timer!";
        }
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        Rectangle bounds = this.getBounds();
        int width = (int) bounds.getWidth(), height = (int) bounds.getHeight();
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.red);
        g.drawString(timeMessage, 2, 32);
    }

    public void updateTime() {
        long newTime = System.currentTimeMillis();
        double deltaSeconds = (newTime - oldTime) / 1000.0;
        oldTime = newTime;
        currentPlayer.changeTime(-deltaSeconds);
    }

    public void checkTimeLeft() {
        if (whitePlayer.outOfTime() || blackPlayer.outOfTime()) {
            this.game.updateWin();
        }
    }

    public void resetTime() {
        this.setActive(false);
        this.currentPlayer = this.whitePlayer;
        this.whitePlayer.setTime(this.initTime);
        this.blackPlayer.setTime(this.initTime);
        this.repaint();
    }

    public void setUntimed(boolean untimed) {
        this.doTime = !untimed;
    }
}