package Chess.Games.UI.Windows;

import javax.swing.JFrame;

import Chess.Main;
import Chess.Games.Variant;
import Online.Packets.CancelGamePacket;

public class GameWindow extends JFrame {
    private Variant game;

    public GameWindow(Variant newGame) {
        super();
        setGame(newGame);

        setSize(1200, 800);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        GameWindow ref = this;

        Runnable cancelAction = new Runnable() {
            @Override
            public void run() {
                while (ref.isShowing()) {
                    System.out.println("running");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("disabled");
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
            game.getParent().remove(game);
        }

        this.game = newGame;
        this.add(this.game);
        pack();
    }

    public Variant getGame(Variant game) {
        return this.game;
    }
}