package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class PendingGamePacket implements Serializable {
    private Variant game;

    public PendingGamePacket(Variant game) {
        this.game = game;
    }

    public Variant getGame() {
        return this.game;
    }
}
