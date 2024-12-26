package Online.Packets;

import Chess.Games.Variant;
import java.io.Serializable;

public class GamePacket implements Serializable {
    private Variant game;

    public GamePacket(Variant game) {
        this.game = game;
    }

    public Variant getGame() {
        return this.game;
    }
}
