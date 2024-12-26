package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class CancelGamePacket implements Serializable {
    private Variant game;

    public CancelGamePacket(Variant game) {
        this.game = game;
    }

    public Variant getGame() {
        return this.game;
    }
}
