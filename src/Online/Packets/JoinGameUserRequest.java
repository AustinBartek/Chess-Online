package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class JoinGameUserRequest implements Serializable {
    private final long ID;
    private final String username;
    private final Variant game;

    public JoinGameUserRequest(long ID, String username, Variant game) {
        this.ID = ID;
        this.username = username;
        this.game = game;
    }

    public long getID() {
        return this.ID;
    }

    public String getUsername() {
        return this.username;
    }

    public Variant getGame() {
        return this.game;
    }
}
