package Chess.UI.Windows;

import javax.swing.JFrame;

import Chess.Main;
import Chess.Games.Variant;
import Online.Packets.CancelGamePacket;

public class GameWindow extends JFrame {
    private Variant game;

    public GameWindow(Variant newGame) {
        super();
        GameWindow ref = this;

        setGame(newGame);

        setSize(1200, 800);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        Runnable cancelAction = new Runnable() {
            @Override
            public void run() {
                while (ref.isShowing()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (game.isOnline()) {
                    if (Main.getClient() != null) {
                        Main.getClient().sendPacket(new CancelGamePacket(newGame));
                    }
                }
            }
        };
        Thread cancelThread = new Thread(cancelAction);
        cancelThread.start();
    }

    public void setGame(Variant newGame) {
        if (game != null) {
            remove(game);
        }

        this.game = newGame;
        this.add(this.game);

        pack();
        repaint();
    }

    public Variant getGame() {
        return this.game;
    }
}