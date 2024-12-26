package Online.Packets;

import java.io.Serializable;

import Chess.Games.Variant;

public class UpdateGamePacket implements Serializable {
    private Variant game;

    public UpdateGamePacket(Variant game) {
        this.game = game;
    }

    public Variant getGame() {
        return this.game;
    }
}
