package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class JoinGamePacket implements Serializable {
    private Variant game;

    public JoinGamePacket(Variant game) {
        this.game = game;
    }

    public Variant getGame() {
        return this.game;
    }
}
