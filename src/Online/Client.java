package Online;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JOptionPane;

import Chess.Games.Variant;
import Chess.UI.Windows.GameWindow;
import Chess.UI.Windows.JoinGameWindow;
import Online.Packets.*;

public class Client {

    private Socket client;
    private Thread mainThread;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isValid = false, hasRun = false, joinPending = false;
    private GameWindow gameWindow = null;
    private HashMap<String, Boolean> settings;

    private static final String dads = "192.168.0.165", school = "10.2.32.207", moms = "192.168.0.17", address = dads;

    public Client() {
        settings = new HashMap<>();
        settings.put("boardFlip", false);
    }

    public void setBoardFlip(boolean flip) {
        settings.put("boardFlip", flip);
    }

    public boolean getBoardFlip() {
        return settings.get("boardFlip");
    }

    public void runClient() {
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                try {
                    client = new Socket(address, 9999);
                    out = new ObjectOutputStream(client.getOutputStream());
                    in = new ObjectInputStream(client.getInputStream());
                    hasRun = true;

                    Object newObject = null;
                    do {
                        try {
                            newObject = in.readObject();
                            handlePacket(newObject);
                        } catch (Exception e) {
                        }
                    } while (newObject != null);

                } catch (Exception e) {
                    shutdown();
                }
            }
        };

        mainThread = new Thread(runner);
        mainThread.start();
    }

    public void handlePacket(Object packet) {
        if (packet instanceof MessagePacket) {

            MessagePacket mp = (MessagePacket) packet;
            System.out.println(mp.getMessage());

        } else if (packet instanceof AllowJoinServerPacket) {

            isValid = true;

        } else if (packet instanceof UsernameRequest ur) {

            if (ur.getFirst()) {
                String newUsername = JOptionPane.showInputDialog("Welcome! Please enter a username: ");

                if (newUsername == null) {
                    return;
                }
                sendPacket(new UsernamePacket(newUsername));
            } else {
                String newUsername = JOptionPane
                        .showInputDialog("That username is taken/invalid, please try another one: ");

                if (newUsername == null) {
                    return;
                }
                sendPacket(new UsernamePacket(newUsername));
            }

        } else if (packet instanceof JoinGameWindowPacket jgwp) {

            new JoinGameWindow(jgwp.getList());

        } else if (packet instanceof JoinGameUserRequest jgur) {

            String username = jgur.getUsername();

            Runnable popupTimer = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                        JOptionPane.getRootFrame().dispose();
                    } catch (InterruptedException e) {
                        JOptionPane.getRootFrame().dispose();
                    }
                }
            };
            Thread timerThread = new Thread(popupTimer);
            timerThread.start();

            boolean allow = JOptionPane.showConfirmDialog(null, username + " wants to play! Allow them to join?") == 0;
            if (allow) {
                sendPacket(new StartGamePacket(jgur.getID(), jgur.getGame()));
            } else {
                sendPacket(new JoinGameDeniedPacket(jgur.getID()));
            }
        } else if (packet instanceof JoinGameDeniedUserPacket) {

            setPendingGame(false);
            JOptionPane.showMessageDialog(null, "Your request to join the game was denied/timed out!");

        } else if (packet instanceof PendingGamePacket pgp) {

            Variant newGame = pgp.getGame();
            newGame.resetListeners();
            setPendingGame(true);
            createGameWindow(newGame);

        } else if (packet instanceof UpdateGamePacket ugp) {

            Variant update = ugp.getGame();
            update.setBoardFlip(this.getBoardFlip());
            update.resetListeners();

            if (this.gameWindow == null || !this.gameWindow.isShowing()) {
                createGameWindow(update);
            } else {
                this.gameWindow.setGame(update);
            }

        } else if (packet instanceof MovePacket mp) {

            this.gameWindow.getGame().selectTile(mp.getPosX(), mp.getPosY(), mp.getRightClick());

        }

        else if (packet instanceof ExitOnlineGamePacket) {

            this.gameWindow.dispose();
            setPendingGame(false);
            JOptionPane.showMessageDialog(null, "Sorry! Your game has been disconnected!");

        } else {

            System.out.println(packet);

        }
    }

    public void sendPacket(Object packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    public void createGameWindow(Variant game) {
        this.gameWindow = new GameWindow(game);
    }

    public boolean getValid() {
        return this.isValid;
    }

    public boolean isActive() {
        if (this.client == null) {
            return false;
        }
        return this.client.isConnected();
    }

    public boolean isOutOfGame() {
        return this.gameWindow == null || (!this.gameWindow.isShowing());
    }

    public boolean canJoinGame() {
        return !this.joinPending && isOutOfGame();
    }

    public void setPendingGame(boolean pending) {
        this.joinPending = pending;
    }

    public void shutdown() {
        try {
            if (client != null) {
                if (!client.isClosed()) {
                    if (out != null) {
                        sendPacket(new QuitPacket());
                    }
                    client.close();
                }
            }
            client.shutdownInput();
            client.shutdownOutput();

        } catch (Exception e) {
        }
        if (!hasRun) {
            JOptionPane.showMessageDialog(null, "Couldn't join the server!");
        }
    }
}