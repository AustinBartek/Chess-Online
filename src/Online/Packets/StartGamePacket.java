package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class StartGamePacket implements Serializable {
    private final long ID;
    private final Variant game;

    public StartGamePacket(long ID, Variant game) {
        this.ID = ID;
        this.game = game;
    }

    public long getID() {
        return this.ID;
    }

    public Variant getGame() {
        return this.game;
    }
}
