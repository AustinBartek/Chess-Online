package Online;

import Chess.Games.Variant;
import Online.Packets.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static Long uniqueGameID = 0l;
    private ArrayList<ConnectionHandler> connections;
    private ArrayList<Matchup> matches;
    private ArrayList<Variant> games;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        matches = new ArrayList<>();
        games = new ArrayList<>();
        done = false;
    }

    public void runServer() {

        try {
            InetAddress address = InetAddress.getByName("192.168.0.165");
            server = new ServerSocket(9999);
            server.setSoTimeout(0);
            pool = Executors.newCachedThreadPool();
            System.out.println("Server Started");

            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }

        } catch (IOException e) {
            shutdown();
        }
    }

    public Matchup getMatchFromPlayer(ConnectionHandler ch) {
        for (Matchup match : matches) {
            if (match.whitePlayer == ch || match.blackPlayer == ch) {
                return match;
            }
        }
        return null;
    }

    public Matchup getMatchFromGame(Variant game) {
        Matchup useMatch = null;
        for (Matchup match : matches) {
            if (Objects.equals(match.id, game.getID())) {
                useMatch = match;
            }
        }
        return useMatch;
    }

    public void broadcast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch == null) {
                continue;
            }
            ch.sendPacket(new MessagePacket(message));
        }
    }

    public void sendGameToPlayers(Matchup match, Variant game) {
        UpdateGamePacket ugp = new UpdateGamePacket(game);
        match.whitePlayer.sendPacket(ugp);
        match.blackPlayer.sendPacket(ugp);
    }

    public void removeGame(Variant gameToRemove) {
        Long gameID = gameToRemove.getID();

        Variant useGame = getGameByID(gameID);
        games.remove(useGame);

        Matchup useMatch = null;
        for (Matchup match : matches) {
            if (Objects.equals(gameID, match.id)) {
                useMatch = match;
                break;
            }
        }

        if (useMatch != null) {
            ConnectionHandler w = useMatch.whitePlayer, b = useMatch.blackPlayer;
            ExitOnlineGamePacket exitPacket = new ExitOnlineGamePacket();
            if (w != null) {
                w.sendPacket(exitPacket);
            }
            if (b != null) {
                b.sendPacket(exitPacket);
            }
            matches.remove(useMatch);
        }
    }

    public void removeGame(Matchup match) {
        if (match == null) {
            return;
        }

        Long gameID = match.getID();

        Variant useGame = getGameByID(gameID);
        games.remove(useGame);

        ConnectionHandler w = match.whitePlayer, b = match.blackPlayer;
        ExitOnlineGamePacket exitPacket = new ExitOnlineGamePacket();
        if (w != null) {
            w.sendPacket(exitPacket);
        }
        if (b != null) {
            b.sendPacket(exitPacket);
        }
        matches.remove(match);
    }

    public void updateGame(Variant game) {
        games.remove(getGameByID(game.getID()));
        games.add(game);
    }

    public Variant getGameByID(Long ID) {
        for (Variant game : games) {
            if (game.getID() == ID) {
                return game;
            }
        }
        return null;
    }

    public ConnectionHandler getHandlerByID(long ID) {
        for (ConnectionHandler ch : connections) {
            if (ch.ID == ID) {
                return ch;
            }
        }
        return null;
    }

    public void shutdown() {
        System.out.println("Shutting Down Server");
        try {
            done = true;
            if (!server.isClosed()) {
                server.close();
            }

            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    /*
     * Class used to handle checking if players are in a match, what color they are
     * supposed to be, and who they are matched against.
     */
    class Matchup {
        private ConnectionHandler whitePlayer, blackPlayer;
        private final Long id;

        public Matchup(ConnectionHandler whitePlayer, ConnectionHandler blackPlayer, Long id) {
            this.whitePlayer = whitePlayer;
            this.blackPlayer = blackPlayer;
            this.id = id;
        }

        public Matchup(ConnectionHandler player, boolean white, Long id) {
            if (white) {
                this.whitePlayer = player;
                this.blackPlayer = null;
            } else {
                this.whitePlayer = null;
                this.blackPlayer = player;
            }
            this.id = id;
        }

        public ConnectionHandler getWhitePlayer() {
            return this.whitePlayer;
        }

        public ConnectionHandler getBlackPlayer() {
            return this.blackPlayer;
        }

        public ConnectionHandler getOnlyPlayer() {
            if (this.whitePlayer == null && this.blackPlayer != null) {
                return this.blackPlayer;
            } else if (this.whitePlayer != null && this.blackPlayer == null) {
                return this.whitePlayer;
            }
            return null;
        }

        public Long getID() {
            return this.id;
        }
    }

    class ConnectionHandler implements Runnable {

        private static long currentID = 0;
        private final Socket client;
        private final long ID;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;
        private Boolean white;

        public ConnectionHandler(Socket client) {
            this.client = client;
            this.username = null;
            this.white = null;

            this.ID = currentID;
            currentID++;
        }

        public boolean isValid() {
            if (this.client == null) {
                return false;
            }
            return true;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());

                System.out.println(client.getInetAddress().getHostAddress());

                sendPacket(new UsernameRequest(true));

                Object newObject;
                while ((newObject = in.readObject()) != null) {
                    handlePacket(newObject);
                }

            } catch (Exception e) {
                shutdown();
            }
        }

        public void handlePacket(Object packet) {
            System.out.println("Handling packet: " + packet.getClass().getName());

            // receiving a message from a client
            if (packet instanceof MessagePacket mp) {

                System.out.println(mp.getMessage());

            }
            // handling a request to set a username
            else if (packet instanceof UsernamePacket up) {

                String newName = up.getUsername();
                boolean valid = validUsername(newName);

                if (valid) {
                    username = newName;
                    sendPacket(new AllowJoinServerPacket());

                    System.out
                            .println("Set username of " + client.getInetAddress().getHostAddress() + " to " + username);
                    sendPacket(new MessagePacket("Welcome to the server, " + username));
                } else {
                    sendPacket(new UsernameRequest(false));
                }
            }
            // handling a request to quit the server
            else if (packet instanceof QuitPacket) {

                shutdown();

            }
            // handling the creation of a game
            else if (packet instanceof CreateGameRequest gp) {

                Variant game = gp.getGame();
                addGame(game, gp.getColor());
                sendPacket(new PendingGamePacket(game));

            }
            // sends a list of players that have a game waiting for a second player
            else if (packet instanceof JoinGameWindowRequest) {

                HashMap<String, Variant> inactiveGames = getInactiveGames();
                JoinGameWindowPacket response = new JoinGameWindowPacket(inactiveGames);
                sendPacket(response);

            }
            //asks the game's owner to allow the player to join the game (for security/playing purposes) 
            else if (packet instanceof JoinGameRequest jgr) {

                Variant game = jgr.getGame();
                Matchup match = getMatchFromGame(game);
                if (match == null) {
                    return;
                }
                ConnectionHandler user = match.getOnlyPlayer();
                if (user == null) {
                    return;
                }
                JoinGameUserRequest request = new JoinGameUserRequest(ID, username, game);
                user.sendPacket(request);

            } else if (packet instanceof JoinGameDeniedPacket jgdp) {

                ConnectionHandler user = getHandlerByID(jgdp.getID());
                if (user == null) {
                    return;
                }
                user.sendPacket(new JoinGameDeniedUserPacket());

            } else if (packet instanceof StartGamePacket sgp) {

                startGame(sgp.getID(), sgp.getGame());

            } else if (packet instanceof UpdateGameRequest ugr) {

                Variant update = ugr.getGame();
                updateGame(update);
                sendGameToPlayers(getMatchFromPlayer(this), update);

            } else if (packet instanceof CancelGamePacket cgp) {

                Variant gameToRemove = cgp.getGame();
                removeGame(gameToRemove);

            } else if (packet instanceof MoveRequest mr) {
                if (mr.getCurrentColor() == this.white) {
                    sendPacket(new MovePacket(mr.getPosX(), mr.getPosY(), mr.getRightClick()));
                }
            }
        }

        public void sendPacket(Object packet) {
            if (this.client.isConnected() && !this.client.isOutputShutdown()) {
                try {
                    out.writeObject(packet);
                    out.flush();
                } catch (IOException e) {
                    if (!(e instanceof SocketException)) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void startGame(long otherID, Variant newGame) {
            Matchup useMatch = getMatchFromGame(newGame);
            ConnectionHandler otherPlayer = getHandlerByID(otherID);
            if (otherPlayer == null || useMatch == null) {
                return;
            }

            if (useMatch.whitePlayer != null) {
                useMatch.blackPlayer = otherPlayer;
                otherPlayer.white = false;
            } else if (useMatch.blackPlayer != null) {
                useMatch.whitePlayer = otherPlayer;
                otherPlayer.white = true;
            }
            newGame.setReady();
            sendGameToPlayers(useMatch, newGame);
        }

        public boolean validUsername(String name) {
            boolean taken = false;
            for (ConnectionHandler ch : connections) {
                if (ch.username == null) {
                    continue;
                }
                if (ch.username.equals(name)) {
                    taken = true;
                }
            }

            boolean allowed = true;
            if (name.replaceAll(" ", "").length() < 1) {
                allowed = false;
            }

            return allowed && !taken;
        }

        public HashMap<String, Variant> getInactiveGames() {
            HashMap<String, Variant> players = new HashMap<>();
            for (Matchup match : matches) {
                if (match.getWhitePlayer() == this || match.getBlackPlayer() == this) {
                    continue;
                }
                if (match.getWhitePlayer() == null || match.getBlackPlayer() == null) {
                    Variant game = getGameByID(match.getID());
                    if (game == null) {
                        continue;
                    }
                    if (match.getWhitePlayer() == null) {
                        players.put(match.getBlackPlayer().username, game);
                    } else {
                        players.put(match.getWhitePlayer().username, game);
                    }
                }
            }
            return players;
        }

        public void addGame(Variant newGame, boolean color) {
            if (getMatchFromPlayer(this) != null) {
                sendPacket(new MessagePacket("Cannot create new game! You are already in an existing game"));
                return;
            }

            this.white = color;

            Long newID = uniqueGameID;
            uniqueGameID++;

            newGame.setID(newID);
            games.add(newGame);
            Matchup newMatch = new Matchup(this, white, newID);
            matches.add(newMatch);

            System.out.println("Created new " + newGame.getGameType() + " game with id: " + newID + ". "
                    + username + " is the " + white + " player.");
        }

        public void shutdown() {
            removeGame(getMatchFromPlayer(this));

            System.out.println(username + " left the server");

            try {
                client.shutdownInput();
                client.shutdownOutput();
                if (!client.isClosed()) {
                    client.close();
                }
                connections.remove(this);
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.runServer();
    }
}

/*
 * Runnable clientChecker = () -> {
 * try {
 * while (true) {
 * Thread.sleep(2000);
 * refreshClients();
 * }
 * } catch (InterruptedException e) {
 * }
 * };
 * Thread clientCheckerThread = new Thread(clientChecker);
 * clientCheckerThread.start();
 */

/*
 * public void refreshClients() {
 * ArrayList<ConnectionHandler> removeList = new ArrayList<>();
 * for (ConnectionHandler ch : connections) {
 * System.out.println(ch.username + " " + ch.isValid());
 * if (!ch.isValid()) {
 * removeList.add(ch);
 * System.out.println(ch.username + " was disconnected");
 * }
 * }
 * for (ConnectionHandler ch : removeList) {
 * 
 * Matchup currentMatch = getMatchFromPlayer(ch);
 * if (currentMatch != null) {
 * removeGame(currentMatch);
 * }
 * ch.shutdown();
 * 
 * }
 * }
 */